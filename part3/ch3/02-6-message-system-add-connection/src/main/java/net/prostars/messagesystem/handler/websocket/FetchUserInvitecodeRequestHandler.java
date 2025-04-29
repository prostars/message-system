package net.prostars.messagesystem.handler.websocket;

import net.prostars.messagesystem.constant.Constants;
import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.dto.domain.UserId;
import net.prostars.messagesystem.dto.websocket.inbound.FetchUserInvitecodeRequest;
import net.prostars.messagesystem.dto.websocket.outbound.ErrorResponse;
import net.prostars.messagesystem.dto.websocket.outbound.FetchUserInvitecodeResponse;
import net.prostars.messagesystem.service.UserService;
import net.prostars.messagesystem.session.WebSocketSessionManager;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
@SuppressWarnings("unused")
public class FetchUserInvitecodeRequestHandler
    implements BaseRequestHandler<FetchUserInvitecodeRequest> {

  private final UserService userService;
  private final WebSocketSessionManager webSocketSessionManager;

  public FetchUserInvitecodeRequestHandler(
      UserService userService, WebSocketSessionManager webSocketSessionManager) {
    this.userService = userService;
    this.webSocketSessionManager = webSocketSessionManager;
  }

  @Override
  public void handleRequest(WebSocketSession senderSession, FetchUserInvitecodeRequest request) {
    UserId senderUserId = (UserId) senderSession.getAttributes().get(Constants.USER_ID.getValue());
    userService
        .getInviteCode(senderUserId)
        .ifPresentOrElse(
            inviteCode ->
                webSocketSessionManager.sendMessage(
                    senderSession, new FetchUserInvitecodeResponse(inviteCode)),
            () ->
                webSocketSessionManager.sendMessage(
                    senderSession,
                    new ErrorResponse(
                        MessageType.FETCH_USER_INVITECODE_REQUEST,
                        "Fetch user invite code failed.")));
  }
}
