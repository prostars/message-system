package net.prostars.messagesystem.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import net.prostars.messagesystem.dto.Message;
import net.prostars.messagesystem.repository.MessageRepository;
import net.prostars.messagesystem.session.WebSocketSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Service
public class MessageService {

  private static final Logger log = LoggerFactory.getLogger(MessageService.class);

  private final ObjectMapper objectMapper = new ObjectMapper();
  private final WebSocketSessionManager webSocketSessionManager;
  private final MessageDataService messageDataService;
  private final MessageRepository messageRepository;

  public MessageService(
      WebSocketSessionManager webSocketSessionManager,
      MessageDataService messageDataService,
      MessageRepository messageRepository) {
    this.webSocketSessionManager = webSocketSessionManager;
    this.messageDataService = messageDataService;
    this.messageRepository = messageRepository;
  }

  public Optional<Message> getLastMessage() {
    return messageRepository
        .findTopByOrderByMessageSequenceDesc()
        .map(
            messageEntity ->
                new Message(
                    messageEntity.getUsername(),
                    messageEntity.getMessageSequence() + ":" + messageEntity.getContent()));
  }

  @Cacheable(value = "message", unless = "#result == null")
  public Optional<Message> getMessage(Long messageSequenceId) {
    return messageRepository
        .findById(messageSequenceId)
        .map(
            messageEntity ->
                new Message(
                    messageEntity.getUsername(),
                    messageEntity.getMessageSequence() + ":" + messageEntity.getContent()));
  }

  @Transactional
  @CacheEvict(value = "message", allEntries = true)
  public void sendMessageToAll(WebSocketSession senderSession, String payload) {
    try {
      Message receivedMessage = objectMapper.readValue(payload, Message.class);
      boolean makeException = receivedMessage.content().equals("/exception");
      messageDataService.save(receivedMessage, makeException);
      webSocketSessionManager
          .getSessions()
          .forEach(
              participantSession -> {
                if (!senderSession.getId().equals(participantSession.getId())) {
                  sendMessage(participantSession, receivedMessage);
                }
              });
    } catch (Exception ex) {
      if (TransactionSynchronizationManager.isActualTransactionActive()) {
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      }

      String errorMessage = "Invalid protocol.";
      log.error("errorMessage payload: {} from {}", payload, senderSession.getId());
      sendMessage(senderSession, new Message("system", errorMessage));
    }
  }

  public void sendMessage(WebSocketSession session, Message message) {
    try {
      String msg = objectMapper.writeValueAsString(message);
      session.sendMessage(new TextMessage(msg));
      log.info("Send message: {} to {}", msg, session.getId());
    } catch (Exception ex) {
      log.error("Failed to send message to {} error: {}", session.getId(), ex.getMessage());
    }
  }
}
