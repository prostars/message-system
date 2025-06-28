package net.prostars.messagesystem.handler.websocket;

import java.util.Optional;
import net.prostars.messagesystem.constant.IdKey;
import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.dto.domain.UserId;
import net.prostars.messagesystem.dto.websocket.inbound.AcceptRequest;
import net.prostars.messagesystem.dto.websocket.outbound.AcceptNotification;
import net.prostars.messagesystem.dto.websocket.outbound.AcceptResponse;
import net.prostars.messagesystem.dto.websocket.outbound.ErrorResponse;
import net.prostars.messagesystem.service.ClientNotificationService;
import net.prostars.messagesystem.service.UserConnectionService;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
@SuppressWarnings("unused")
public class AcceptRequestHandler implements BaseRequestHandler<AcceptRequest> {

  private final UserConnectionService userConnectionService;
  private final ClientNotificationService clientNotificationService;

  public AcceptRequestHandler(
      UserConnectionService userConnectionService,
      ClientNotificationService clientNotificationService) {
    this.userConnectionService = userConnectionService;
    this.clientNotificationService = clientNotificationService;
  }

  @Override
  public void handleRequest(WebSocketSession senderSession, AcceptRequest request) {
    UserId acceptorUserId = (UserId) senderSession.getAttributes().get(IdKey.USER_ID.getValue());
    Pair<Optional<UserId>, String> result =
        userConnectionService.accept(acceptorUserId, request.getUsername());
    result
        .getFirst()
        .ifPresentOrElse(
            inviterUserId -> {
              clientNotificationService.sendMessage(
                  senderSession, acceptorUserId, new AcceptResponse(request.getUsername()));
              String acceptorUsername = result.getSecond();
              clientNotificationService.sendMessage(
                  inviterUserId, new AcceptNotification(acceptorUsername));
            },
            () -> {
              String errorMessage = result.getSecond();
              clientNotificationService.sendMessage(
                  senderSession,
                  acceptorUserId,
                  new ErrorResponse(MessageType.ACCEPT_REQUEST, errorMessage));
            });
  }
}
