package net.prostars.messagesystem.handler.websocket;

import net.prostars.messagesystem.constant.IdKey;
import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.dto.domain.UserId;
import net.prostars.messagesystem.dto.kafka.QuitRequestRecord;
import net.prostars.messagesystem.dto.websocket.inbound.QuitRequest;
import net.prostars.messagesystem.dto.websocket.outbound.ErrorResponse;
import net.prostars.messagesystem.kafka.KafkaProducer;
import net.prostars.messagesystem.service.ClientNotificationService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
@SuppressWarnings("unused")
public class QuitRequestHandler implements BaseRequestHandler<QuitRequest> {

  private final KafkaProducer kafkaProducer;
  private final ClientNotificationService clientNotificationService;

  public QuitRequestHandler(
      KafkaProducer kafkaProducer, ClientNotificationService clientNotificationService) {
    this.kafkaProducer = kafkaProducer;
    this.clientNotificationService = clientNotificationService;
  }

  @Override
  public void handleRequest(WebSocketSession senderSession, QuitRequest request) {
    UserId senderUserId = (UserId) senderSession.getAttributes().get(IdKey.USER_ID.getValue());
    kafkaProducer.sendRequest(
        new QuitRequestRecord(senderUserId, request.getChannelId()),
        () ->
            clientNotificationService.sendErrorMessage(
                senderSession, new ErrorResponse(MessageType.QUIT_REQUEST, "Quit failed.")));
  }
}
