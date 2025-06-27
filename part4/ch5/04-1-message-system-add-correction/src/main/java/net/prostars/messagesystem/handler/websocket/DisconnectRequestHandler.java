package net.prostars.messagesystem.handler.websocket;

import net.prostars.messagesystem.constant.IdKey;
import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.constant.UserConnectionStatus;
import net.prostars.messagesystem.dto.domain.UserId;
import net.prostars.messagesystem.dto.websocket.inbound.DisconnectRequest;
import net.prostars.messagesystem.dto.websocket.outbound.DisconnectResponse;
import net.prostars.messagesystem.dto.websocket.outbound.ErrorResponse;
import net.prostars.messagesystem.service.ClientNotificationService;
import net.prostars.messagesystem.service.UserConnectionService;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
@SuppressWarnings("unused")
public class DisconnectRequestHandler implements BaseRequestHandler<DisconnectRequest> {

  private final UserConnectionService userConnectionService;
  private final ClientNotificationService clientNotificationService;

  public DisconnectRequestHandler(
      UserConnectionService userConnectionService,
      ClientNotificationService clientNotificationService) {
    this.userConnectionService = userConnectionService;
    this.clientNotificationService = clientNotificationService;
  }

  @Override
  public void handleRequest(WebSocketSession senderSession, DisconnectRequest request) {
    UserId senderUserId = (UserId) senderSession.getAttributes().get(IdKey.USER_ID.getValue());
    Pair<Boolean, String> result =
        userConnectionService.disconnect(senderUserId, request.getUsername());
    if (result.getFirst()) {
      clientNotificationService.sendMessage(
          senderSession,
          senderUserId,
          new DisconnectResponse(request.getUsername(), UserConnectionStatus.DISCONNECTED));
    } else {
      String errorMessage = result.getSecond();
      clientNotificationService.sendMessage(
          senderSession,
          senderUserId,
          new ErrorResponse(MessageType.DISCONNECT_REQUEST, errorMessage));
    }
  }
}
