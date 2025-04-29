package net.prostars.messagesystem.handler.websocket;

import net.prostars.messagesystem.dto.websocket.inbound.BaseRequest;
import org.springframework.web.socket.WebSocketSession;

public interface BaseRequestHandler<T extends BaseRequest> {
  void handleRequest(WebSocketSession webSocketSession, T request);
}
