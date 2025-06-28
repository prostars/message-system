package net.prostars.messagesystem.handler.websocket;

import net.prostars.messagesystem.constant.IdKey;
import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.dto.domain.ChannelId;
import net.prostars.messagesystem.dto.domain.UserId;
import net.prostars.messagesystem.dto.kafka.WriteMessageRecord;
import net.prostars.messagesystem.dto.websocket.inbound.WriteMessage;
import net.prostars.messagesystem.dto.websocket.outbound.ErrorResponse;
import net.prostars.messagesystem.kafka.KafkaProducer;
import net.prostars.messagesystem.service.ClientNotificationService;
import net.prostars.messagesystem.service.MessageSeqIdGenerator;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
@SuppressWarnings("unused")
public class WriteMessageHandler implements BaseRequestHandler<WriteMessage> {

  private final KafkaProducer kafkaProducer;
  private final ClientNotificationService clientNotificationService;
  private final MessageSeqIdGenerator messageSeqIdGenerator;

  public WriteMessageHandler(
      KafkaProducer kafkaProducer,
      ClientNotificationService clientNotificationService,
      MessageSeqIdGenerator messageSeqIdGenerator) {
    this.kafkaProducer = kafkaProducer;
    this.clientNotificationService = clientNotificationService;
    this.messageSeqIdGenerator = messageSeqIdGenerator;
  }

  @Override
  public void handleRequest(WebSocketSession senderSession, WriteMessage request) {
    UserId senderUserId = (UserId) senderSession.getAttributes().get(IdKey.USER_ID.getValue());
    ChannelId channelId = request.getChannelId();
    Runnable errorCallback =
        () ->
            clientNotificationService.sendErrorMessage(
                senderSession,
                new ErrorResponse(MessageType.WRITE_MESSAGE, "Write message failed."));

    messageSeqIdGenerator
        .getNext(channelId)
        .ifPresentOrElse(
            messageSeqId ->
                kafkaProducer.sendMessageUsingPartitionKey(
                    channelId,
                    senderUserId,
                    new WriteMessageRecord(
                        senderUserId,
                        request.getSerial(),
                        channelId,
                        request.getContent(),
                        messageSeqId),
                    errorCallback),
            errorCallback);
  }
}
