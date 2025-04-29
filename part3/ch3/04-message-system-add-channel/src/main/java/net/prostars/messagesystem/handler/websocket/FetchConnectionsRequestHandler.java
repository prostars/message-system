package net.prostars.messagesystem.handler.websocket;

import java.util.List;
import net.prostars.messagesystem.constant.IdKey;
import net.prostars.messagesystem.dto.domain.Connection;
import net.prostars.messagesystem.dto.domain.UserId;
import net.prostars.messagesystem.dto.websocket.inbound.FetchConnectionsRequest;
import net.prostars.messagesystem.dto.websocket.outbound.FetchConnectionsResponse;
import net.prostars.messagesystem.service.UserConnectionService;
import net.prostars.messagesystem.session.WebSocketSessionManager;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
@SuppressWarnings("unused")
public class FetchConnectionsRequestHandler implements BaseRequestHandler<FetchConnectionsRequest> {

  private final UserConnectionService userConnectionService;
  private final WebSocketSessionManager webSocketSessionManager;

  public FetchConnectionsRequestHandler(
      UserConnectionService userConnectionService,
      WebSocketSessionManager webSocketSessionManager) {
    this.userConnectionService = userConnectionService;
    this.webSocketSessionManager = webSocketSessionManager;
  }

  @Override
  public void handleRequest(WebSocketSession senderSession, FetchConnectionsRequest request) {
    UserId senderUserId = (UserId) senderSession.getAttributes().get(IdKey.USER_ID.getValue());
    List<Connection> connections =
        userConnectionService.getUsersByStatus(senderUserId, request.getStatus()).stream()
            .map(user -> new Connection(user.username(), request.getStatus()))
            .toList();
    webSocketSessionManager.sendMessage(senderSession, new FetchConnectionsResponse(connections));
  }
}
