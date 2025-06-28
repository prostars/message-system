package net.prostars.messagesystem.service;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.function.Function;
import net.prostars.messagesystem.constant.KeyPrefix;
import net.prostars.messagesystem.constant.UserConnectionStatus;
import net.prostars.messagesystem.dto.domain.UserId;
import net.prostars.messagesystem.entity.UserConnectionEntity;
import net.prostars.messagesystem.entity.UserEntity;
import net.prostars.messagesystem.repository.UserConnectionRepository;
import net.prostars.messagesystem.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserConnectionLimitService {

  private final CacheService cacheService;
  private final UserRepository userRepository;
  private final UserConnectionRepository userConnectionRepository;
  private int limitConnections = 1_000;

  public UserConnectionLimitService(
      CacheService cacheService,
      UserRepository userRepository,
      UserConnectionRepository userConnectionRepository) {
    this.cacheService = cacheService;
    this.userRepository = userRepository;
    this.userConnectionRepository = userConnectionRepository;
  }

  public int getLimitConnections() {
    return limitConnections;
  }

  public void setLimitConnections(int limitConnections) {
    this.limitConnections = limitConnections;
  }

  @Transactional
  public void accept(UserId acceptorUserId, UserId inviterUserId) {
    Long firstUserId = Long.min(acceptorUserId.id(), inviterUserId.id());
    Long secondUserId = Long.max(acceptorUserId.id(), inviterUserId.id());
    UserEntity firstUserEntity =
        userRepository
            .findForUpdateByUserId(firstUserId)
            .orElseThrow(() -> new EntityNotFoundException("Invalid userId: " + firstUserId));
    UserEntity secondUserEntity =
        userRepository
            .findForUpdateByUserId(secondUserId)
            .orElseThrow(() -> new EntityNotFoundException("Invalid userId: " + secondUserId));
    UserConnectionEntity userConnectionEntity =
        userConnectionRepository
            .findByPartnerAUserIdAndPartnerBUserIdAndStatus(
                firstUserId, secondUserId, UserConnectionStatus.PENDING)
            .orElseThrow(() -> new EntityNotFoundException("Invalid status."));

    Function<Long, String> getErrorMessage =
        userId ->
            userId.equals(acceptorUserId.id())
                ? "Connection limit reached."
                : "Connection limit reached by the other user.";

    int firstConnectionCount = firstUserEntity.getConnectionCount();
    if (firstConnectionCount >= limitConnections) {
      throw new IllegalStateException(getErrorMessage.apply(firstUserId));
    }
    int secondConnectionCount = secondUserEntity.getConnectionCount();
    if (secondConnectionCount >= limitConnections) {
      throw new IllegalStateException(getErrorMessage.apply(secondUserId));
    }

    firstUserEntity.setConnectionCount(firstConnectionCount + 1);
    secondUserEntity.setConnectionCount(secondConnectionCount + 1);
    userConnectionEntity.setStatus(UserConnectionStatus.ACCEPTED);
    cacheService.delete(
        List.of(
            cacheService.buildKey(
                KeyPrefix.CONNECTION_STATUS,
                String.valueOf(firstUserId),
                String.valueOf(secondUserId)),
            cacheService.buildKey(
                KeyPrefix.CONNECTIONS_STATUS,
                acceptorUserId.id().toString(),
                UserConnectionStatus.ACCEPTED.name()),
            cacheService.buildKey(
                KeyPrefix.CONNECTIONS_STATUS,
                inviterUserId.id().toString(),
                UserConnectionStatus.ACCEPTED.name())));
  }

  @Transactional
  public void disconnect(UserId senderUserId, UserId partnerUserId) {
    Long firstUserId = Long.min(senderUserId.id(), partnerUserId.id());
    Long secondUserId = Long.max(senderUserId.id(), partnerUserId.id());
    UserEntity firstUserEntity =
        userRepository
            .findForUpdateByUserId(firstUserId)
            .orElseThrow(() -> new EntityNotFoundException("Invalid userId: " + firstUserId));
    UserEntity secondUserEntity =
        userRepository
            .findForUpdateByUserId(secondUserId)
            .orElseThrow(() -> new EntityNotFoundException("Invalid userId: " + secondUserId));
    UserConnectionEntity userConnectionEntity =
        userConnectionRepository
            .findByPartnerAUserIdAndPartnerBUserIdAndStatus(
                firstUserId, secondUserId, UserConnectionStatus.ACCEPTED)
            .orElseThrow(() -> new EntityNotFoundException("Invalid status."));

    int firstConnectionCount = firstUserEntity.getConnectionCount();
    if (firstConnectionCount <= 0) {
      throw new IllegalStateException("Count is already zero. userId: " + firstUserId);
    }
    int secondConnectionCount = secondUserEntity.getConnectionCount();
    if (secondConnectionCount <= 0) {
      throw new IllegalStateException("Count is already zero. userId: " + senderUserId);
    }

    firstUserEntity.setConnectionCount(firstConnectionCount - 1);
    secondUserEntity.setConnectionCount(secondConnectionCount - 1);
    userConnectionEntity.setStatus(UserConnectionStatus.DISCONNECTED);
    cacheService.delete(
        List.of(
            cacheService.buildKey(
                KeyPrefix.CONNECTION_STATUS,
                String.valueOf(firstUserId),
                String.valueOf(secondUserId)),
            cacheService.buildKey(
                KeyPrefix.CONNECTIONS_STATUS,
                senderUserId.id().toString(),
                UserConnectionStatus.DISCONNECTED.name()),
            cacheService.buildKey(
                KeyPrefix.CONNECTIONS_STATUS,
                partnerUserId.id().toString(),
                UserConnectionStatus.DISCONNECTED.name())));
  }
}
