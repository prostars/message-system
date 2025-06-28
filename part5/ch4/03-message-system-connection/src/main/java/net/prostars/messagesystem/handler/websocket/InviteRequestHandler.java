package net.prostars.messagesystem.handler.websocket;

import net.prostars.messagesystem.constant.IdKey;
import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.dto.domain.UserId;
import net.prostars.messagesystem.dto.kafka.InviteRequestRecord;
import net.prostars.messagesystem.dto.websocket.inbound.InviteRequest;
import net.prostars.messagesystem.dto.websocket.outbound.ErrorResponse;
import net.prostars.messagesystem.kafka.KafkaProducer;
import net.prostars.messagesystem.service.ClientNotificationService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
@SuppressWarnings("unused")
public class InviteRequestHandler implements BaseRequestHandler<InviteRequest> {

  private final KafkaProducer kafkaProducer;
  private final ClientNotificationService clientNotificationService;

  public InviteRequestHandler(
      KafkaProducer kafkaProducer, ClientNotificationService clientNotificationService) {
    this.kafkaProducer = kafkaProducer;
    this.clientNotificationService = clientNotificationService;
  }

  @Override
  public void handleRequest(WebSocketSession senderSession, InviteRequest request) {
    UserId inviterUserId = (UserId) senderSession.getAttributes().get(IdKey.USER_ID.getValue());
    kafkaProducer.sendRequest(
        new InviteRequestRecord(inviterUserId, request.getUserInviteCode()),
        () ->
            clientNotificationService.sendErrorMessage(
                senderSession, new ErrorResponse(MessageType.INVITE_REQUEST, "Invite failed.")));
  }
}
