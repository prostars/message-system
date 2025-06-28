package net.prostars.messagesystem.service;

import java.util.List;
import net.prostars.messagesystem.constant.KeyPrefix;
import net.prostars.messagesystem.dto.domain.UserId;
import net.prostars.messagesystem.entity.UserEntity;
import net.prostars.messagesystem.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

  private static final Logger log = LoggerFactory.getLogger(UserService.class);
  private final SessionService sessionService;
  private final CacheService cacheService;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public UserService(
      SessionService sessionService,
      CacheService cacheService,
      UserRepository userRepository,
      PasswordEncoder passwordEncoder) {
    this.sessionService = sessionService;
    this.cacheService = cacheService;
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Transactional
  public UserId addUser(String username, String password) {
    UserEntity userEntity =
        userRepository.save(new UserEntity(username, passwordEncoder.encode(password)));
    log.info(
        "User registered. UserId: {}, Username: {}",
        userEntity.getUserId(),
        userEntity.getUsername());
    return new UserId(userEntity.getUserId());
  }

  @Transactional
  public void removeUser() {
    String username = sessionService.getUsername();
    UserEntity userEntity = userRepository.findByUsername(username).orElseThrow();
    String userId = userEntity.getUserId().toString();
    userRepository.deleteById(userEntity.getUserId());
    cacheService.delete(
        List.of(
            cacheService.buildKey(KeyPrefix.USER_ID, username),
            cacheService.buildKey(KeyPrefix.USERNAME, userId),
            cacheService.buildKey(KeyPrefix.USER, userId),
            cacheService.buildKey(KeyPrefix.USER_INVITECODE, userId)));

    log.info(
        "User unregistered. UserId: {}, Username: {}",
        userEntity.getUserId(),
        userEntity.getUsername());
  }
}
