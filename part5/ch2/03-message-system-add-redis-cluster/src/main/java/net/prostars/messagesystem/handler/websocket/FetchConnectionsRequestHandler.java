package net.prostars.messagesystem.handler.websocket;

import java.util.List;
import net.prostars.messagesystem.constant.IdKey;
import net.prostars.messagesystem.dto.domain.Connection;
import net.prostars.messagesystem.dto.domain.UserId;
import net.prostars.messagesystem.dto.websocket.inbound.FetchConnectionsRequest;
import net.prostars.messagesystem.dto.websocket.outbound.FetchConnectionsResponse;
import net.prostars.messagesystem.service.ClientNotificationService;
import net.prostars.messagesystem.service.UserConnectionService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
@SuppressWarnings("unused")
public class FetchConnectionsRequestHandler implements BaseRequestHandler<FetchConnectionsRequest> {

  private final UserConnectionService userConnectionService;
  private final ClientNotificationService clientNotificationService;

  public FetchConnectionsRequestHandler(
      UserConnectionService userConnectionService,
      ClientNotificationService clientNotificationService) {
    this.userConnectionService = userConnectionService;
    this.clientNotificationService = clientNotificationService;
  }

  @Override
  public void handleRequest(WebSocketSession senderSession, FetchConnectionsRequest request) {
    UserId senderUserId = (UserId) senderSession.getAttributes().get(IdKey.USER_ID.getValue());
    List<Connection> connections =
        userConnectionService.getUsersByStatus(senderUserId, request.getStatus()).stream()
            .map(user -> new Connection(user.username(), request.getStatus()))
            .toList();
    clientNotificationService.sendMessage(
        senderSession, senderUserId, new FetchConnectionsResponse(connections));
  }
}
