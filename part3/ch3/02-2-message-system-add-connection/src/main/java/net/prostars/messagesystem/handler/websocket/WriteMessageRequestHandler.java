package net.prostars.messagesystem.handler.websocket;

import net.prostars.messagesystem.dto.domain.Message;
import net.prostars.messagesystem.dto.websocket.inbound.WriteMessageRequest;
import net.prostars.messagesystem.entity.MessageEntity;
import net.prostars.messagesystem.repository.MessageRepository;
import net.prostars.messagesystem.session.WebSocketSessionManager;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
@SuppressWarnings("unused")
public class WriteMessageRequestHandler implements BaseRequestHandler<WriteMessageRequest> {

  private final WebSocketSessionManager webSocketSessionManager;
  private final MessageRepository messageRepository;

  public WriteMessageRequestHandler(
      WebSocketSessionManager webSocketSessionManager, MessageRepository messageRepository) {
    this.webSocketSessionManager = webSocketSessionManager;
    this.messageRepository = messageRepository;
  }

  @Override
  public void handleRequest(WebSocketSession senderSession, WriteMessageRequest request) {
    Message receivedMessage = new Message(request.getUsername(), request.getContent());
    messageRepository.save(
        new MessageEntity(receivedMessage.username(), receivedMessage.content()));
    webSocketSessionManager
        .getSessions()
        .forEach(
            participantSession -> {
              if (!senderSession.getId().equals(participantSession.getId())) {
                webSocketSessionManager.sendMessage(participantSession, receivedMessage);
              }
            });
  }
}
