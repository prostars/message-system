package net.prostars.messagesystem.service;

import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.dto.domain.ChannelId;
import net.prostars.messagesystem.dto.domain.UserId;
import net.prostars.messagesystem.dto.kafka.*;
import net.prostars.messagesystem.json.JsonUtil;
import net.prostars.messagesystem.kafka.KafkaProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ClientNotificationService {

  private static final Logger log = LoggerFactory.getLogger(ClientNotificationService.class);

  private final SessionService sessionService;
  private final KafkaProducer kafkaProducer;
  private final PushService pushService;
  private final JsonUtil jsonUtil;

  public ClientNotificationService(
      SessionService sessionService,
      KafkaProducer kafkaProducer,
      PushService pushService,
      JsonUtil jsonUtil) {
    this.sessionService = sessionService;
    this.kafkaProducer = kafkaProducer;
    this.pushService = pushService;
    this.jsonUtil = jsonUtil;

    pushService.registerPushMessageType(MessageType.INVITE_RESPONSE, InviteResponseRecord.class);
    pushService.registerPushMessageType(MessageType.ASK_INVITE, InviteNotificationRecord.class);
    pushService.registerPushMessageType(MessageType.ACCEPT_RESPONSE, AcceptResponseRecord.class);
    pushService.registerPushMessageType(MessageType.NOTIFY_ACCEPT, AcceptNotificationRecord.class);
    pushService.registerPushMessageType(MessageType.NOTIFY_JOIN, JoinNotificationRecord.class);
    pushService.registerPushMessageType(
        MessageType.DISCONNECT_RESPONSE, DisconnectResponseRecord.class);
    pushService.registerPushMessageType(MessageType.REJECT_RESPONSE, RejectResponseRecord.class);
    pushService.registerPushMessageType(MessageType.CREATE_RESPONSE, CreateResponseRecord.class);
    pushService.registerPushMessageType(MessageType.QUIT_RESPONSE, QuitResponseRecord.class);
  }

  public void sendMessage(UserId userId, RecordInterface recordInterface) {
    sessionService
        .getListenTopic(userId)
        .ifPresentOrElse(
            topic -> kafkaProducer.sendResponse(topic, recordInterface),
            () -> pushService.pushMessage(recordInterface));
  }

  public void sendMessageUsingPartitionKey(
      ChannelId channelId, UserId userId, RecordInterface recordInterface) {
    sessionService
        .getListenTopic(userId)
        .ifPresentOrElse(
            topic ->
                kafkaProducer.sendMessageUsingPartitionKey(
                    topic, channelId, userId, recordInterface),
            () -> pushService.pushMessage(recordInterface));
  }

  public void sendError(ErrorResponseRecord errorResponseRecord) {
    sessionService
        .getListenTopic(errorResponseRecord.userId())
        .ifPresentOrElse(
            topic -> kafkaProducer.sendResponse(topic, errorResponseRecord),
            () ->
                log.warn(
                    "Send error failed. Type: {}, Error: {}, UserId: {} is offline.",
                    errorResponseRecord.messageType(),
                    errorResponseRecord.message(),
                    errorResponseRecord.userId()));
  }
}
