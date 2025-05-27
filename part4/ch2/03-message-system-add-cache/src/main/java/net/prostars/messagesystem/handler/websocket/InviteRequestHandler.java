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
import net.prostars.messagesystem.service.ClientNotificationService;
import net.prostars.messagesystem.service.UserConnectionService;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
@SuppressWarnings("unused")
public class InviteRequestHandler implements BaseRequestHandler<InviteRequest> {

  private final UserConnectionService userConnectionService;
  private final ClientNotificationService clientNotificationService;

  public InviteRequestHandler(
      UserConnectionService userConnectionService,
      ClientNotificationService clientNotificationService) {
    this.userConnectionService = userConnectionService;
    this.clientNotificationService = clientNotificationService;
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
              clientNotificationService.sendMessage(
                  senderSession,
                  inviterUserId,
                  new InviteResponse(request.getUserInviteCode(), UserConnectionStatus.PENDING));
              clientNotificationService.sendMessage(
                  partnerUserId, new InviteNotification(inviterUsername));
            },
            () -> {
              String errorMessage = result.getSecond();
              clientNotificationService.sendMessage(
                  senderSession,
                  inviterUserId,
                  new ErrorResponse(MessageType.INVITE_REQUEST, errorMessage));
            });
  }
}
