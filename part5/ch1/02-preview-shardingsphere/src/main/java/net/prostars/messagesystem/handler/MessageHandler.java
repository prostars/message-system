package net.prostars.messagesystem.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.prostars.messagesystem.dto.Message;
import net.prostars.messagesystem.entity.MessageEntity;
import net.prostars.messagesystem.repository.MessageRepository;
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
  private final ObjectMapper objectMapper = new ObjectMapper();
  private final WebSocketSessionManager webSocketSessionManager;
  private final MessageRepository messageRepository;

  public MessageHandler(
      WebSocketSessionManager webSocketSessionManager, MessageRepository messageRepository) {
    this.webSocketSessionManager = webSocketSessionManager;
    this.messageRepository = messageRepository;
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
    try {
      Message receivedMessage = objectMapper.readValue(payload, Message.class);
      messageRepository.save(
          new MessageEntity(receivedMessage.channelId(), receivedMessage.username(), receivedMessage.content()));

      webSocketSessionManager
          .getSessions()
          .forEach(
              participantSession -> {
                if (!senderSession.getId().equals(participantSession.getId())) {
                  sendMessage(participantSession, receivedMessage);
                }
              });
    } catch (Exception ex) {
      String errorMessage = "Invalid protocol.";
      log.error("errorMessage payload: {} from {}", payload, senderSession.getId());
      sendMessage(senderSession, new Message(0L, "system", errorMessage));
    }
  }

  private void sendMessage(WebSocketSession session, Message message) {
    try {
      String msg = objectMapper.writeValueAsString(message);
      session.sendMessage(new TextMessage(msg));
      log.info("Send message: {} to {}", msg, session.getId());
    } catch (Exception ex) {
      log.error("Failed to send message to {} error: {}", session.getId(), ex.getMessage());
    }
  }
}
