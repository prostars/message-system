package net.prostars.messagesystem.service;

import java.util.List;
import java.util.Optional;
import net.prostars.messagesystem.constant.KeyPrefix;
import net.prostars.messagesystem.dto.domain.InviteCode;
import net.prostars.messagesystem.dto.domain.User;
import net.prostars.messagesystem.dto.domain.UserId;
import net.prostars.messagesystem.dto.projection.CountProjection;
import net.prostars.messagesystem.dto.projection.UsernameProjection;
import net.prostars.messagesystem.entity.UserEntity;
import net.prostars.messagesystem.json.JsonUtil;
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
  private final JsonUtil jsonUtil;
  private final long TTL = 3600;

  public UserService(
      SessionService sessionService,
      CacheService cacheService,
      UserRepository userRepository,
      PasswordEncoder passwordEncoder,
      JsonUtil jsonUtil) {
    this.sessionService = sessionService;
    this.cacheService = cacheService;
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jsonUtil = jsonUtil;
  }

  @Transactional(readOnly = true)
  public Optional<String> getUsername(UserId userId) {
    String key = cacheService.buildKey(KeyPrefix.USERNAME, userId.id().toString());
    Optional<String> cachedUsername = cacheService.get(key);
    if (cachedUsername.isPresent()) {
      return cachedUsername;
    }

    Optional<String> fromDb =
        userRepository.findByUserId(userId.id()).map(UsernameProjection::getUsername);
    fromDb.ifPresent(username -> cacheService.set(key, username, TTL));
    return fromDb;
  }

  @Transactional(readOnly = true)
  public Optional<UserId> getUserId(String username) {
    String key = cacheService.buildKey(KeyPrefix.USER_ID, username);
    Optional<String> cachedUserId = cacheService.get(key);
    if (cachedUserId.isPresent()) {
      return Optional.of(new UserId(Long.valueOf(cachedUserId.get())));
    }

    Optional<UserId> fromDb =
        userRepository
            .findUserIdByUsername(username)
            .map(projection -> new UserId(projection.getUserId()));
    fromDb.ifPresent(userId -> cacheService.set(key, userId.id().toString(), TTL));
    return fromDb;
  }

  @Transactional(readOnly = true)
  public List<UserId> getUserIds(List<String> usernames) {
    return userRepository.findByUsernameIn(usernames).stream()
        .map(projection -> new UserId(projection.getUserId()))
        .toList();
  }

  @Transactional(readOnly = true)
  public Optional<User> getUser(InviteCode inviteCode) {
    String key = cacheService.buildKey(KeyPrefix.USER, inviteCode.code());
    Optional<String> cachedUser = cacheService.get(key);
    if (cachedUser.isPresent()) {
      return jsonUtil.fromJson(cachedUser.get(), User.class);
    }

    Optional<User> fromDb =
        userRepository
            .findByInviteCode(inviteCode.code())
            .map(entity -> new User(new UserId(entity.getUserId()), entity.getUsername()));
    fromDb.flatMap(jsonUtil::toJson).ifPresent(json -> cacheService.set(key, json, TTL));
    return fromDb;
  }

  @Transactional(readOnly = true)
  public Optional<InviteCode> getInviteCode(UserId userId) {
    String key = cacheService.buildKey(KeyPrefix.USER_INVITECODE, userId.id().toString());
    Optional<String> cachedInviteCode = cacheService.get(key);
    if (cachedInviteCode.isPresent()) {
      return Optional.of(new InviteCode(cachedInviteCode.get()));
    }

    Optional<InviteCode> fromDb =
        userRepository
            .findInviteCodeByUserId(userId.id())
            .map(inviteCode -> new InviteCode(inviteCode.getInviteCode()));
    fromDb.ifPresent(inviteCode -> cacheService.set(key, inviteCode.code(), TTL));
    return fromDb;
  }

  @Transactional(readOnly = true)
  public Optional<Integer> getConnectionCount(UserId userId) {
    return userRepository.findCountByUserId(userId.id()).map(CountProjection::getConnectionCount);
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
