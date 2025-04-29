package net.prostars.messagesystem.handler.websocket;

import java.util.Optional;
import net.prostars.messagesystem.constant.Constants;
import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.dto.domain.UserId;
import net.prostars.messagesystem.dto.websocket.inbound.AcceptRequest;
import net.prostars.messagesystem.dto.websocket.outbound.AcceptNotification;
import net.prostars.messagesystem.dto.websocket.outbound.AcceptResponse;
import net.prostars.messagesystem.dto.websocket.outbound.ErrorResponse;
import net.prostars.messagesystem.service.UserConnectionService;
import net.prostars.messagesystem.session.WebSocketSessionManager;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
@SuppressWarnings("unused")
public class AcceptRequestHandler implements BaseRequestHandler<AcceptRequest> {

  private final UserConnectionService userConnectionService;
  private final WebSocketSessionManager webSocketSessionManager;

  public AcceptRequestHandler(
      UserConnectionService userConnectionService,
      WebSocketSessionManager webSocketSessionManager) {
    this.userConnectionService = userConnectionService;
    this.webSocketSessionManager = webSocketSessionManager;
  }

  @Override
  public void handleRequest(WebSocketSession senderSession, AcceptRequest request) {
    UserId acceptorUserId =
        (UserId) senderSession.getAttributes().get(Constants.USER_ID.getValue());
    Pair<Optional<UserId>, String> result =
        userConnectionService.accept(acceptorUserId, request.getUsername());
    result
        .getFirst()
        .ifPresentOrElse(
            inviterUserId -> {
              webSocketSessionManager.sendMessage(
                  senderSession, new AcceptResponse(request.getUsername()));
              String acceptorUsername = result.getSecond();
              webSocketSessionManager.sendMessage(
                  webSocketSessionManager.getSession(inviterUserId),
                  new AcceptNotification(acceptorUsername));
            },
            () -> {
              String errorMessage = result.getSecond();
              webSocketSessionManager.sendMessage(
                  senderSession, new ErrorResponse(MessageType.ACCEPT_REQUEST, errorMessage));
            });
  }
}
