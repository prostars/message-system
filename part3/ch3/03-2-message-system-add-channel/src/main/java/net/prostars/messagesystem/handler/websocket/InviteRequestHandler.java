package net.prostars.messagesystem.handler.websocket;

import java.util.Optional;
import net.prostars.messagesystem.constant.IdKey;
import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.constant.UserConnectionStatus;
import net.prostars.messagesystem.dto.domain.UserId;
import net.prostars.messagesystem.dto.websocket.inbound.InviteRequest;
import net.prostars.messagesystem.dto.websocket.outbound.ErrorResponse;
import net.prostars.messagesystem.dto.websocket.outbound.InviteNotification;
import net.prostars.messagesystem.dto.websocket.outbound.InviteResponse;
import net.prostars.messagesystem.service.UserConnectionService;
import net.prostars.messagesystem.session.WebSocketSessionManager;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
@SuppressWarnings("unused")
public class InviteRequestHandler implements BaseRequestHandler<InviteRequest> {

  private final UserConnectionService userConnectionService;
  private final WebSocketSessionManager webSocketSessionManager;

  public InviteRequestHandler(
      UserConnectionService userConnectionService,
      WebSocketSessionManager webSocketSessionManager) {
    this.userConnectionService = userConnectionService;
    this.webSocketSessionManager = webSocketSessionManager;
  }

  @Override
  public void handleRequest(WebSocketSession senderSession, InviteRequest request) {
    UserId inviterUserId = (UserId) senderSession.getAttributes().get(IdKey.USER_ID.getValue());
    Pair<Optional<UserId>, String> result =
        userConnectionService.invite(inviterUserId, request.getUserInviteCode());
    result
        .getFirst()
        .ifPresentOrElse(
            partnerUserId -> {
              String inviterUsername = result.getSecond();
              webSocketSessionManager.sendMessage(
                  senderSession,
                  new InviteResponse(request.getUserInviteCode(), UserConnectionStatus.PENDING));
              webSocketSessionManager.sendMessage(
                  webSocketSessionManager.getSession(partnerUserId),
                  new InviteNotification(inviterUsername));
            },
            () -> {
              String errorMessage = result.getSecond();
              webSocketSessionManager.sendMessage(
                  senderSession, new ErrorResponse(MessageType.INVITE_REQUEST, errorMessage));
            });
  }
}
