package net.prostars.messagesystem.handler.websocket;

import net.prostars.messagesystem.constant.IdKey;
import net.prostars.messagesystem.dto.domain.UserId;
import net.prostars.messagesystem.dto.websocket.inbound.KeepAlive;
import net.prostars.messagesystem.service.SessionService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
@SuppressWarnings("unused")
public class KeepAliveHandler implements BaseRequestHandler<KeepAlive> {

  private final SessionService sessionService;

  public KeepAliveHandler(SessionService sessionService) {
    this.sessionService = sessionService;
  }

  @Override
  public void handleRequest(WebSocketSession senderSession, KeepAlive request) {
    UserId senderUserId = (UserId) senderSession.getAttributes().get(IdKey.USER_ID.getValue());

    sessionService.refreshTTL(
        senderUserId, (String) senderSession.getAttributes().get(IdKey.HTTP_SESSION_ID.getValue()));
  }
}
