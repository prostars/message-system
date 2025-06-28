package net.prostars.messagesystem.handler.websocket;

import net.prostars.messagesystem.constant.IdKey;
import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.dto.domain.ChannelId;
import net.prostars.messagesystem.dto.domain.UserId;
import net.prostars.messagesystem.dto.kafka.FetchMessagesRequestRecord;
import net.prostars.messagesystem.dto.websocket.inbound.FetchMessagesRequest;
import net.prostars.messagesystem.dto.websocket.outbound.ErrorResponse;
import net.prostars.messagesystem.kafka.KafkaProducer;
import net.prostars.messagesystem.service.ClientNotificationService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
@SuppressWarnings("unused")
public class FetchMessagesRequestHandler implements BaseRequestHandler<FetchMessagesRequest> {

  private final KafkaProducer kafkaProducer;
  private final ClientNotificationService clientNotificationService;

  public FetchMessagesRequestHandler(
      KafkaProducer kafkaProducer, ClientNotificationService clientNotificationService) {
    this.kafkaProducer = kafkaProducer;
    this.clientNotificationService = clientNotificationService;
  }

  @Override
  public void handleRequest(WebSocketSession senderSession, FetchMessagesRequest request) {
    UserId senderUserId = (UserId) senderSession.getAttributes().get(IdKey.USER_ID.getValue());
    ChannelId channelId = request.getChannelId();
    kafkaProducer.sendMessageUsingPartitionKey(
        channelId,
        senderUserId,
        new FetchMessagesRequestRecord(
            senderUserId, channelId, request.getStartMessageSeqId(), request.getEndMessageSeqId()),
        () ->
            clientNotificationService.sendErrorMessage(
                senderSession,
                new ErrorResponse(MessageType.FETCH_MESSAGES_REQUEST, "Fetch messages failed.")));
  }
}
