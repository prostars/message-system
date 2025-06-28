package net.prostars.messagesystem.service;

import java.util.*;
import java.util.stream.Collectors;
import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.constant.ResultType;
import net.prostars.messagesystem.dto.domain.ChannelId;
import net.prostars.messagesystem.dto.domain.Message;
import net.prostars.messagesystem.dto.domain.MessageSeqId;
import net.prostars.messagesystem.dto.domain.UserId;
import net.prostars.messagesystem.dto.kafka.MessageNotificationRecord;
import net.prostars.messagesystem.dto.kafka.WriteMessageAckRecord;
import net.prostars.messagesystem.dto.kafka.WriteMessageRecord;
import net.prostars.messagesystem.dto.projection.MessageInfoProjection;
import net.prostars.messagesystem.kafka.KafkaProducer;
import net.prostars.messagesystem.repository.UserChannelRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MessageService {

  private static final Logger log = LoggerFactory.getLogger(MessageService.class);

  private final UserService userService;
  private final ChannelService channelService;
  private final MessageShardService messageShardService;
  private final PushService pushService;
  private final SessionService sessionService;
  private final KafkaProducer kafkaProducer;
  private final UserChannelRepository userChannelRepository;

  public MessageService(
      UserService userService,
      ChannelService channelService,
      MessageShardService messageShardService,
      PushService pushService,
      SessionService sessionService,
      KafkaProducer kafkaProducer,
      UserChannelRepository userChannelRepository) {
    this.userService = userService;
    this.channelService = channelService;
    this.messageShardService = messageShardService;
    this.pushService = pushService;
    this.sessionService = sessionService;
    this.kafkaProducer = kafkaProducer;

    pushService.registerPushMessageType(
        MessageType.NOTIFY_MESSAGE, MessageNotificationRecord.class);
    this.userChannelRepository = userChannelRepository;
  }

  @Transactional(readOnly = true)
  public Pair<List<Message>, ResultType> getMessages(
      ChannelId channelId, MessageSeqId startMessageSeqId, MessageSeqId endMessageSeqId) {
    List<MessageInfoProjection> messageInfos =
        messageShardService.findByChannelIdAndMessageSequenceBetween(
            channelId, startMessageSeqId, endMessageSeqId);
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
  public void sendMessage(WriteMessageRecord record) {
    ChannelId channelId = record.channelId();
    UserId senderUserId = record.userId();
    MessageSeqId messageSeqId = record.messageSeqId();
    String senderUsername = userService.getUsername(senderUserId).orElse("unknown");

    try {
      messageShardService.save(channelId, messageSeqId, senderUserId, record.content());
    } catch (Exception ex) {
      log.error("Send message failed. cause: {}", ex.getMessage());
      return;
    }

    List<UserId> allParticipantIds = channelService.getParticipantIds(channelId);
    List<UserId> onlineParticipantIds =
        channelService.getOnlineParticipantIds(channelId, allParticipantIds);
    Map<String, List<UserId>> listenTopics = sessionService.getListenTopics(onlineParticipantIds);
    allParticipantIds.removeAll(onlineParticipantIds);

    listenTopics.forEach(
        (listenTopic, participantIds) -> {
          if (participantIds.contains(senderUserId)) {
            updateLastReadMsgSeq(senderUserId, channelId, messageSeqId);
            kafkaProducer.sendMessageUsingPartitionKey(
                listenTopic,
                channelId,
                senderUserId,
                new WriteMessageAckRecord(senderUserId, record.serial(), messageSeqId));
            participantIds.remove(senderUserId);
          } else {
            kafkaProducer.sendMessageUsingPartitionKey(
                listenTopic,
                channelId,
                senderUserId,
                new MessageNotificationRecord(
                    senderUserId,
                    channelId,
                    messageSeqId,
                    senderUsername,
                    record.content(),
                    participantIds));
          }
        });

    if (!allParticipantIds.isEmpty()) {
      pushService.pushMessage(
          new MessageNotificationRecord(
              senderUserId,
              channelId,
              messageSeqId,
              senderUsername,
              record.content(),
              allParticipantIds));
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
