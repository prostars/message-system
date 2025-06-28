package net.prostars.messagesystem.handler.websocket;

import net.prostars.messagesystem.constant.IdKey;
import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.dto.domain.UserId;
import net.prostars.messagesystem.dto.kafka.DisconnectRequestRecord;
import net.prostars.messagesystem.dto.websocket.inbound.DisconnectRequest;
import net.prostars.messagesystem.dto.websocket.outbound.ErrorResponse;
import net.prostars.messagesystem.kafka.KafkaProducer;
import net.prostars.messagesystem.service.ClientNotificationService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
@SuppressWarnings("unused")
public class DisconnectRequestHandler implements BaseRequestHandler<DisconnectRequest> {

  private final KafkaProducer kafkaProducer;
  private final ClientNotificationService clientNotificationService;

  public DisconnectRequestHandler(
      KafkaProducer kafkaProducer, ClientNotificationService clientNotificationService) {
    this.kafkaProducer = kafkaProducer;
    this.clientNotificationService = clientNotificationService;
  }

  @Override
  public void handleRequest(WebSocketSession senderSession, DisconnectRequest request) {
    UserId senderUserId = (UserId) senderSession.getAttributes().get(IdKey.USER_ID.getValue());

    kafkaProducer.sendRequest(
        new DisconnectRequestRecord(senderUserId, request.getUsername()),
        () ->
            clientNotificationService.sendErrorMessage(
                senderSession,
                new ErrorResponse(MessageType.DISCONNECT_REQUEST, "Disconnect failed.")));
  }
}
