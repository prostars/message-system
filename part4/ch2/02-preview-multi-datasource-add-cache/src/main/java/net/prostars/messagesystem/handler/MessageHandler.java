package net.prostars.messagesystem.handler;

import net.prostars.messagesystem.dto.Message;
import net.prostars.messagesystem.service.MessageService;
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
public class MessageHandler extends TextWebSocketHandler {

  private static final Logger log = LoggerFactory.getLogger(MessageHandler.class);
  private final WebSocketSessionManager webSocketSessionManager;
  private final MessageService messageService;

  public MessageHandler(
      WebSocketSessionManager webSocketSessionManager, MessageService messageService) {
    this.webSocketSessionManager = webSocketSessionManager;
    this.messageService = messageService;
  }

  @Override
  public void afterConnectionEstablished(WebSocketSession session) {
    log.info("ConnectionEstablished: {}", session.getId());
    ConcurrentWebSocketSessionDecorator concurrentWebSocketSessionDecorator =
        new ConcurrentWebSocketSessionDecorator(session, 5000, 100 * 1024);
    webSocketSessionManager.storeSession(concurrentWebSocketSessionDecorator);
  }

  @Override
  public void handleTransportError(WebSocketSession session, Throwable exception) {
    log.error("TransportError: [{}] from {}", exception.getMessage(), session.getId());
    webSocketSessionManager.terminateSession(session.getId());
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, @NonNull CloseStatus status) {
    log.info("ConnectionClosed: [{}] from {}", status, session.getId());
    webSocketSessionManager.terminateSession(session.getId());
  }

  @Override
  protected void handleTextMessage(WebSocketSession senderSession, @NonNull TextMessage message) {
    log.info("Received TextMessage: [{}] from {}", message, senderSession.getId());
    String payload = message.getPayload();

    if (payload.equals("/last")) {
      messageService
          .getLastMessage()
          .ifPresent(msg -> messageService.sendMessage(senderSession, msg));
    } else if (payload.contains("/get")) { // ex: /get {number}
      String[] split = payload.split(" ");
      if (split.length > 1) {
        try {
          messageService
              .getMessage(Long.valueOf(split[1]))
              .ifPresent(msg -> messageService.sendMessage(senderSession, msg));
        } catch (Exception ex) {
          String errorMessage = "Invalid protocol.";
          log.error("Get request failed. cause: {}", ex.getMessage());
          messageService.sendMessage(senderSession, new Message("system", errorMessage));
        }
      }
    } else {
      try {
        messageService.sendMessageToAll(senderSession, payload);
      } catch (Exception ex) {
        log.error("Failed to send message to {} error: {}", senderSession.getId(), ex.getMessage());
      }
    }
  }
}
