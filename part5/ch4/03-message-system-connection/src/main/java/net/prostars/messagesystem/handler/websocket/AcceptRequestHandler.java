package net.prostars.messagesystem.handler.websocket;

import net.prostars.messagesystem.constant.IdKey;
import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.dto.domain.UserId;
import net.prostars.messagesystem.dto.kafka.AcceptRequestRecord;
import net.prostars.messagesystem.dto.websocket.inbound.AcceptRequest;
import net.prostars.messagesystem.dto.websocket.outbound.ErrorResponse;
import net.prostars.messagesystem.kafka.KafkaProducer;
import net.prostars.messagesystem.service.ClientNotificationService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
@SuppressWarnings("unused")
public class AcceptRequestHandler implements BaseRequestHandler<AcceptRequest> {

  private final KafkaProducer kafkaProducer;
  private final ClientNotificationService clientNotificationService;

  public AcceptRequestHandler(
      KafkaProducer kafkaProducer, ClientNotificationService clientNotificationService) {
    this.kafkaProducer = kafkaProducer;
    this.clientNotificationService = clientNotificationService;
  }

  @Override
  public void handleRequest(WebSocketSession senderSession, AcceptRequest request) {
    UserId acceptorUserId = (UserId) senderSession.getAttributes().get(IdKey.USER_ID.getValue());
    kafkaProducer.sendRequest(
        new AcceptRequestRecord(acceptorUserId, request.getUsername()),
        () ->
            clientNotificationService.sendErrorMessage(
                senderSession, new ErrorResponse(MessageType.ACCEPT_REQUEST, "Accept failed.")));
  }
}
