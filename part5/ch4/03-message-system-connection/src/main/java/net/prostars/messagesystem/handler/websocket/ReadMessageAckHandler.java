package net.prostars.messagesystem.handler.websocket;

import net.prostars.messagesystem.constant.IdKey;
import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.dto.domain.ChannelId;
import net.prostars.messagesystem.dto.domain.UserId;
import net.prostars.messagesystem.dto.kafka.ReadMessageAckRecord;
import net.prostars.messagesystem.dto.websocket.inbound.ReadMessageAck;
import net.prostars.messagesystem.dto.websocket.outbound.ErrorResponse;
import net.prostars.messagesystem.kafka.KafkaProducer;
import net.prostars.messagesystem.service.ClientNotificationService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
@SuppressWarnings("unused")
public class ReadMessageAckHandler implements BaseRequestHandler<ReadMessageAck> {

  private final KafkaProducer kafkaProducer;
  private final ClientNotificationService clientNotificationService;

  public ReadMessageAckHandler(
      KafkaProducer kafkaProducer, ClientNotificationService clientNotificationService) {
    this.kafkaProducer = kafkaProducer;
    this.clientNotificationService = clientNotificationService;
  }

  @Override
  public void handleRequest(WebSocketSession senderSession, ReadMessageAck request) {
    UserId senderUserId = (UserId) senderSession.getAttributes().get(IdKey.USER_ID.getValue());
    ChannelId channelId = request.getChannelId();
    kafkaProducer.sendMessageUsingPartitionKey(
        channelId,
        senderUserId,
        new ReadMessageAckRecord(senderUserId, channelId, request.getMessageSeqId()),
        () ->
            clientNotificationService.sendErrorMessage(
                senderSession,
                new ErrorResponse(MessageType.READ_MESSAGE_ACK, "Read message ack failed.")));
  }
}
