package net.prostars.messagesystem.handler.websocket;

import net.prostars.messagesystem.constant.IdKey;
import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.dto.domain.UserId;
import net.prostars.messagesystem.dto.websocket.inbound.FetchUserInvitecodeRequest;
import net.prostars.messagesystem.dto.websocket.outbound.ErrorResponse;
import net.prostars.messagesystem.dto.websocket.outbound.FetchUserInvitecodeResponse;
import net.prostars.messagesystem.service.ClientNotificationService;
import net.prostars.messagesystem.service.UserService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
@SuppressWarnings("unused")
public class FetchUserInvitecodeRequestHandler
    implements BaseRequestHandler<FetchUserInvitecodeRequest> {

  private final UserService userService;
  private final ClientNotificationService clientNotificationService;

  public FetchUserInvitecodeRequestHandler(
      UserService userService, ClientNotificationService clientNotificationService) {
    this.userService = userService;
    this.clientNotificationService = clientNotificationService;
  }

  @Override
  public void handleRequest(WebSocketSession senderSession, FetchUserInvitecodeRequest request) {
    UserId senderUserId = (UserId) senderSession.getAttributes().get(IdKey.USER_ID.getValue());
    userService
        .getInviteCode(senderUserId)
        .ifPresentOrElse(
            inviteCode ->
                clientNotificationService.sendMessage(
                    senderSession, senderUserId, new FetchUserInvitecodeResponse(inviteCode)),
            () ->
                clientNotificationService.sendMessage(
                    senderSession,
                    senderUserId,
                    new ErrorResponse(
                        MessageType.FETCH_USER_INVITECODE_REQUEST,
                        "Fetch user invite code failed.")));
  }
}
