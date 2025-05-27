package net.prostars.messagesystem.service;

import net.prostars.messagesystem.dto.Message;
import net.prostars.messagesystem.entity.MessageEntity;
import net.prostars.messagesystem.repository.MessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MessageDataService {

  private static final Logger log = LoggerFactory.getLogger(MessageDataService.class);

  private final MessageRepository messageRepository;

  public MessageDataService(MessageRepository messageRepository) {
    this.messageRepository = messageRepository;
  }

  @Transactional
  public void save(Message message, boolean makeException) {
    try {
      messageRepository.save(new MessageEntity(message.username(), message.content()));
      if (makeException) {
        throw new RuntimeException("For test");
      }
    } catch (Exception ex) {
      log.error("Message save failed. cause: {}", ex.getMessage());
      throw ex;
    }
  }
}
