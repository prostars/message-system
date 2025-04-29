package net.prostars.messagesystem.handler.websocket;

import net.prostars.messagesystem.constant.Constants;
import net.prostars.messagesystem.dto.websocket.inbound.KeepAliveRequest;
import net.prostars.messagesystem.service.SessionService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
@SuppressWarnings("unused")
public class KeepAliveRequestHandler implements BaseRequestHandler<KeepAliveRequest> {

  private final SessionService sessionService;

  public KeepAliveRequestHandler(SessionService sessionService) {
    this.sessionService = sessionService;
  }

  @Override
  public void handleRequest(WebSocketSession senderSession, KeepAliveRequest request) {
    sessionService.refreshTTL(
        (String) senderSession.getAttributes().get(Constants.HTTP_SESSION_ID.getValue()));
  }
}
