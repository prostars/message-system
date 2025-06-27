package net.prostars.messagesystem.service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.constant.ResultType;
import net.prostars.messagesystem.dto.domain.ChannelId;
import net.prostars.messagesystem.dto.domain.Message;
import net.prostars.messagesystem.dto.domain.MessageSeqId;
import net.prostars.messagesystem.dto.domain.UserId;
import net.prostars.messagesystem.dto.kafka.outbound.MessageNotificationRecord;
import net.prostars.messagesystem.dto.projection.MessageInfoProjection;
import net.prostars.messagesystem.dto.websocket.outbound.BaseMessage;
import net.prostars.messagesystem.dto.websocket.outbound.WriteMessageAck;
import net.prostars.messagesystem.entity.MessageEntity;
import net.prostars.messagesystem.json.JsonUtil;
import net.prostars.messagesystem.repository.MessageRepository;
import net.prostars.messagesystem.repository.UserChannelRepository;
import net.prostars.messagesystem.session.WebSocketSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.WebSocketSession;

@Service
public class MessageService {

  private static final Logger log = LoggerFactory.getLogger(MessageService.class);
  private static final int THREAD_POOL_SIZE = 10;

  private final UserService userService;
  private final ChannelService channelService;
  private final PushService pushService;
  private final MessageRepository messageRepository;
  private final WebSocketSessionManager webSocketSessionManager;
  private final JsonUtil jsonUtil;
  private final ExecutorService senderThreadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
  private final UserChannelRepository userChannelRepository;

  public MessageService(
      UserService userService,
      ChannelService channelService,
      PushService pushService,
      MessageRepository messageRepository,
      WebSocketSessionManager webSocketSessionManager,
      JsonUtil jsonUtil,
      UserChannelRepository userChannelRepository) {
    this.userService = userService;
    this.channelService = channelService;
    this.pushService = pushService;
    this.messageRepository = messageRepository;
    this.webSocketSessionManager = webSocketSessionManager;
    this.jsonUtil = jsonUtil;

    pushService.registerPushMessageType(
        MessageType.NOTIFY_MESSAGE, MessageNotificationRecord.class);
    this.userChannelRepository = userChannelRepository;
  }

  @Transactional(readOnly = true)
  public Pair<List<Message>, ResultType> getMessages(
      ChannelId channelId, MessageSeqId startMessageSeqId, MessageSeqId endMessageSeqId) {
    List<MessageInfoProjection> messageInfos =
        messageRepository.findByChannelIdAndMessageSequenceBetween(
            channelId.id(), startMessageSeqId.id(), endMessageSeqId.id());
    Set<UserId> userIds =
        messageInfos.stream()
            .map(proj -> new UserId(proj.getUserId()))
            .collect(Collectors.toUnmodifiableSet());
    if (userIds.isEmpty()) {
      return Pair.of(Collections.emptyList(), ResultType.SUCCESS);
    }

    Pair<Map<UserId, String>, ResultType> result = userService.getUsernames(userIds);
    if (result.getSecond() == ResultType.SUCCESS) {
      List<Message> messages =
          messageInfos.stream()
              .map(
                  proj -> {
                    UserId userId = new UserId(proj.getUserId());
                    return new Message(
                        channelId,
                        new MessageSeqId(proj.getMessageSequence()),
                        result.getFirst().getOrDefault(userId, "unknown"),
                        proj.getContent());
                  })
              .toList();
      return Pair.of(messages, ResultType.SUCCESS);
    } else {
      return Pair.of(Collections.emptyList(), result.getSecond());
    }
  }

  @Transactional
  public void sendMessage(
      UserId senderUserId,
      String content,
      ChannelId channelId,
      MessageSeqId messageSeqId,
      Long serial,
      BaseMessage message) {
    Optional<String> json = jsonUtil.toJson(message);
    if (json.isEmpty()) {
      log.error("Send message failed. MessageType: {}", message.getType());
      return;
    }
    String payload = json.get();

    try {
      messageRepository.save(
          new MessageEntity(channelId.id(), messageSeqId.id(), senderUserId.id(), content));
    } catch (Exception ex) {
      log.error("Send message failed. cause: {}", ex.getMessage());
      return;
    }

    List<UserId> allParticipantIds = channelService.getParticipantIds(channelId);
    List<UserId> onlineParticipantIds =
        channelService.getOnlineParticipantIds(channelId, allParticipantIds);

    for (int idx = 0; idx < allParticipantIds.size(); idx++) {
      UserId participantId = allParticipantIds.get(idx);
      if (senderUserId.equals(participantId)) {
        updateLastReadMsgSeq(senderUserId, channelId, messageSeqId);
        jsonUtil
            .toJson(new WriteMessageAck(serial, messageSeqId))
            .ifPresent(
                writeMessageAck ->
                    CompletableFuture.runAsync(
                        () -> {
                          try {
                            WebSocketSession senderSession =
                                webSocketSessionManager.getSession(senderUserId);
                            if (senderSession != null) {
                              webSocketSessionManager.sendMessage(senderSession, writeMessageAck);
                            }
                          } catch (Exception ex) {
                            log.warn(
                                "Send writeMessageAck failed. userId: {}, cause: {}",
                                senderUserId.id(),
                                ex.getMessage());
                          }
                        },
                        senderThreadPool));
        continue;
      }
      if (onlineParticipantIds.get(idx) != null) {
        CompletableFuture.runAsync(
            () -> {
              try {
                WebSocketSession participantSession =
                    webSocketSessionManager.getSession(participantId);
                if (participantSession != null) {
                  webSocketSessionManager.sendMessage(participantSession, payload);
                } else {
                  pushService.pushMessage(participantId, MessageType.NOTIFY_MESSAGE, payload);
                }
              } catch (Exception ex) {
                pushService.pushMessage(participantId, MessageType.NOTIFY_MESSAGE, payload);
              }
            },
            senderThreadPool);
      } else {
        pushService.pushMessage(participantId, MessageType.NOTIFY_MESSAGE, payload);
      }
    }
  }

  @Transactional
  public void updateLastReadMsgSeq(UserId userId, ChannelId channelId, MessageSeqId messageSeqId) {
    if (userChannelRepository.updateLastReadMsgSeqByUserIdAndChannelId(
            userId.id(), channelId.id(), messageSeqId.id())
        == 0) {
      log.error(
          "Update lastReadMsgSeq failed. No record found for UserId: {} and ChannelId: {}",
          userId.id(),
          channelId.id());
    }
  }
}
