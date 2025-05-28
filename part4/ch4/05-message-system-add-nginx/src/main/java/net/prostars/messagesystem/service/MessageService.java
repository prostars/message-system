package net.prostars.messagesystem.service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.dto.domain.ChannelId;
import net.prostars.messagesystem.dto.domain.UserId;
import net.prostars.messagesystem.dto.kafka.outbound.MessageNotificationRecord;
import net.prostars.messagesystem.dto.websocket.outbound.BaseMessage;
import net.prostars.messagesystem.entity.MessageEntity;
import net.prostars.messagesystem.json.JsonUtil;
import net.prostars.messagesystem.repository.MessageRepository;
import net.prostars.messagesystem.session.WebSocketSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.WebSocketSession;

@Service
public class MessageService {

  private static final Logger log = LoggerFactory.getLogger(MessageService.class);
  private static final int THREAD_POOL_SIZE = 10;

  private final ChannelService channelService;
  private final PushService pushService;
  private final MessageRepository messageRepository;
  private final WebSocketSessionManager webSocketSessionManager;
  private final JsonUtil jsonUtil;
  private final ExecutorService senderThreadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

  public MessageService(
      ChannelService channelService,
      PushService pushService,
      MessageRepository messageRepository,
      WebSocketSessionManager webSocketSessionManager,
      JsonUtil jsonUtil) {
    this.channelService = channelService;
    this.pushService = pushService;
    this.messageRepository = messageRepository;
    this.webSocketSessionManager = webSocketSessionManager;
    this.jsonUtil = jsonUtil;

    pushService.registerPushMessageType(MessageType.NOTIFY_MESSAGE, MessageNotificationRecord.class);
  }

  @Transactional
  public void sendMessage(
      UserId senderUserId, String content, ChannelId channelId, BaseMessage message) {
    Optional<String> json = jsonUtil.toJson(message);
    if (json.isEmpty()) {
      log.error("Send message failed. MessageType: {}", message.getType());
      return;
    }
    String payload = json.get();

    try {
      messageRepository.save(new MessageEntity(senderUserId.id(), content));
    } catch (Exception ex) {
      log.error("Send message failed. cause: {}", ex.getMessage());
      return;
    }

    List<UserId> allParticipantIds = channelService.getParticipantIds(channelId);
    List<UserId> onlineParticipantIds = channelService.getOnlineParticipantIds(channelId, allParticipantIds);

    for (int idx = 0; idx < allParticipantIds.size(); idx++) {
      UserId participantId = allParticipantIds.get(idx);
      if (senderUserId.equals(participantId)) {
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
}
