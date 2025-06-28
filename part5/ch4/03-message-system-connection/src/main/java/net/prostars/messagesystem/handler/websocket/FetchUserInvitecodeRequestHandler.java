package net.prostars.messagesystem.handler.websocket;

import net.prostars.messagesystem.constant.IdKey;
import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.dto.domain.UserId;
import net.prostars.messagesystem.dto.kafka.FetchUserInvitecodeRequestRecord;
import net.prostars.messagesystem.dto.websocket.inbound.FetchUserInvitecodeRequest;
import net.prostars.messagesystem.dto.websocket.outbound.ErrorResponse;
import net.prostars.messagesystem.kafka.KafkaProducer;
import net.prostars.messagesystem.service.ClientNotificationService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
@SuppressWarnings("unused")
public class FetchUserInvitecodeRequestHandler
    implements BaseRequestHandler<FetchUserInvitecodeRequest> {

  private final KafkaProducer kafkaProducer;
  private final ClientNotificationService clientNotificationService;

  public FetchUserInvitecodeRequestHandler(
      KafkaProducer kafkaProducer, ClientNotificationService clientNotificationService) {
    this.kafkaProducer = kafkaProducer;
    this.clientNotificationService = clientNotificationService;
  }

  @Override
  public void handleRequest(WebSocketSession senderSession, FetchUserInvitecodeRequest request) {
    UserId senderUserId = (UserId) senderSession.getAttributes().get(IdKey.USER_ID.getValue());
    kafkaProducer.sendRequest(
        new FetchUserInvitecodeRequestRecord(senderUserId),
        () ->
            clientNotificationService.sendErrorMessage(
                senderSession,
                new ErrorResponse(
                    MessageType.FETCH_USER_INVITECODE_REQUEST, "Fetch user invitecode failed.")));
  }
}
