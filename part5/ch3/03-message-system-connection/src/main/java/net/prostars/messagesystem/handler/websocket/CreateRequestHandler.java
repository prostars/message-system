package net.prostars.messagesystem.handler.websocket;

import net.prostars.messagesystem.constant.IdKey;
import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.dto.domain.UserId;
import net.prostars.messagesystem.dto.kafka.CreateRequestRecord;
import net.prostars.messagesystem.dto.websocket.inbound.CreateRequest;
import net.prostars.messagesystem.dto.websocket.outbound.ErrorResponse;
import net.prostars.messagesystem.kafka.KafkaProducer;
import net.prostars.messagesystem.service.ClientNotificationService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
@SuppressWarnings("unused")
public class CreateRequestHandler implements BaseRequestHandler<CreateRequest> {

  private final KafkaProducer kafkaProducer;
  private final ClientNotificationService clientNotificationService;

  public CreateRequestHandler(
      KafkaProducer kafkaProducer, ClientNotificationService clientNotificationService) {
    this.kafkaProducer = kafkaProducer;
    this.clientNotificationService = clientNotificationService;
  }

  @Override
  public void handleRequest(WebSocketSession senderSession, CreateRequest request) {
    UserId senderUserId = (UserId) senderSession.getAttributes().get(IdKey.USER_ID.getValue());

    kafkaProducer.sendRequest(
        new CreateRequestRecord(
            senderUserId, request.getTitle(), request.getParticipantUsernames()),
        () ->
            clientNotificationService.sendErrorMessage(
                senderSession, new ErrorResponse(MessageType.CREATE_REQUEST, "Create failed.")));
  }
}
