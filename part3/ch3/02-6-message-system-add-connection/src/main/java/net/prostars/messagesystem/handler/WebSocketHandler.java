package net.prostars.messagesystem.handler;

import net.prostars.messagesystem.constant.Constants;
import net.prostars.messagesystem.dto.domain.UserId;
import net.prostars.messagesystem.dto.websocket.inbound.BaseRequest;
import net.prostars.messagesystem.handler.websocket.RequestDispatcher;
import net.prostars.messagesystem.json.JsonUtil;
import net.prostars.messagesystem.session.WebSocketSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class WebSocketHandler extends TextWebSocketHandler {

  private static final Logger log = LoggerFactory.getLogger(WebSocketHandler.class);
  private final JsonUtil jsonUtil;
  private final WebSocketSessionManager webSocketSessionManager;
  private final RequestDispatcher requestDispatcher;

  public WebSocketHandler(
      RequestDispatcher requestDispatcher,
      WebSocketSessionManager webSocketSessionManager,
      JsonUtil jsonUtil) {
    this.requestDispatcher = requestDispatcher;
    this.webSocketSessionManager = webSocketSessionManager;
    this.jsonUtil = jsonUtil;
  }

  @Override
  public void afterConnectionEstablished(WebSocketSession session) {
    log.info("ConnectionEstablished: {}", session.getId());
    ConcurrentWebSocketSessionDecorator concurrentWebSocketSessionDecorator =
        new ConcurrentWebSocketSessionDecorator(session, 5000, 100 * 1024);
    UserId userId = (UserId) session.getAttributes().get(Constants.USER_ID.getValue());
    webSocketSessionManager.putSession(userId, concurrentWebSocketSessionDecorator);
  }

  @Override
  public void handleTransportError(WebSocketSession session, Throwable exception) {
    log.error("TransportError: [{}] from {}", exception.getMessage(), session.getId());
    UserId userId = (UserId) session.getAttributes().get(Constants.USER_ID.getValue());
    webSocketSessionManager.closeSession(userId);
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, @NonNull CloseStatus status) {
    log.info("ConnectionClosed: [{}] from {}", status, session.getId());
    UserId userId = (UserId) session.getAttributes().get(Constants.USER_ID.getValue());
    webSocketSessionManager.closeSession(userId);
  }

  @Override
  protected void handleTextMessage(WebSocketSession senderSession, @NonNull TextMessage message) {
    String payload = message.getPayload();
    log.info("Received TextMessage: [{}] from {}", payload, senderSession.getId());
    jsonUtil
        .fromJson(payload, BaseRequest.class)
        .ifPresent(msg -> requestDispatcher.dispatchRequest(senderSession, msg));
  }
}
