package net.prostars.messagesystem.service;

import net.prostars.messagesystem.dto.domain.UserId;
import net.prostars.messagesystem.entity.MessageUserEntity;
import net.prostars.messagesystem.repository.MessageUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MessageUserService {

  private static final Logger log = LoggerFactory.getLogger(MessageUserService.class);
  private final SessionService sessionService;
  private final MessageUserRepository messageUserRepository;
  private final PasswordEncoder passwordEncoder;

  public MessageUserService(
      SessionService sessionService,
      MessageUserRepository messageUserRepository,
      PasswordEncoder passwordEncoder) {
    this.sessionService = sessionService;
    this.messageUserRepository = messageUserRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Transactional
  public UserId addUser(String username, String password) {
    MessageUserEntity messageUserEntity =
        messageUserRepository.save(
            new MessageUserEntity(username, passwordEncoder.encode(password)));
    log.info(
        "User registered. UserId: {}, Username: {}",
        messageUserEntity.getUserId(),
        messageUserEntity.getUsername());
    return new UserId(messageUserEntity.getUserId());
  }

  @Transactional
  public void removeUser() {
    String username = sessionService.getUsername();
    MessageUserEntity messageUserEntity =
        messageUserRepository.findByUsername(username).orElseThrow();
    messageUserRepository.deleteById(messageUserEntity.getUserId());

    log.info(
        "User unregistered. UserId: {}, Username: {}",
        messageUserEntity.getUserId(),
        messageUserEntity.getUsername());
  }
}
