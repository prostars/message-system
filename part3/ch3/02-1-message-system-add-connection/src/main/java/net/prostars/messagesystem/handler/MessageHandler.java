package net.prostars.messagesystem.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.prostars.messagesystem.constant.Constants;
import net.prostars.messagesystem.dto.domain.Message;
import net.prostars.messagesystem.dto.websocket.inbound.BaseRequest;
import net.prostars.messagesystem.dto.websocket.inbound.KeepAliveRequest;
import net.prostars.messagesystem.dto.websocket.inbound.MessageRequest;
import net.prostars.messagesystem.entity.MessageEntity;
import net.prostars.messagesystem.repository.MessageRepository;
import net.prostars.messagesystem.service.SessionService;
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
  private final SessionService sessionService;
  private final WebSocketSessionManager webSocketSessionManager;
  private final MessageRepository messageRepository;

  public MessageHandler(
      SessionService sessionService,
      WebSocketSessionManager webSocketSessionManager,
      MessageRepository messageRepository) {
    this.sessionService = sessionService;
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
    String payload = message.getPayload();
    log.info("Received TextMessage: [{}] from {}", payload, senderSession.getId());
    try {
      BaseRequest baseRequest = objectMapper.readValue(payload, BaseRequest.class);

      if (baseRequest instanceof MessageRequest messageRequest) {
        Message receivedMessage =
            new Message(messageRequest.getUsername(), messageRequest.getContent());
        messageRepository.save(
            new MessageEntity(receivedMessage.username(), receivedMessage.content()));
        webSocketSessionManager
            .getSessions()
            .forEach(
                participantSession -> {
                  if (!senderSession.getId().equals(participantSession.getId())) {
                    sendMessage(participantSession, receivedMessage);
                  }
                });
      } else if (baseRequest instanceof KeepAliveRequest) {
        sessionService.refreshTTL(
            (String) senderSession.getAttributes().get(Constants.HTTP_SESSION_ID.getValue()));
      }
    } catch (Exception ex) {
      String errorMessage = "Invalid protocol.";
      log.error("errorMessage payload: {} from {}", payload, senderSession.getId());
      sendMessage(senderSession, new Message("system", errorMessage));
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
