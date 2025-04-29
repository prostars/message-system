package net.prostars.messagesystem.service;

import jakarta.persistence.EntityNotFoundException;
import java.util.function.Function;
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

  private final UserRepository userRepository;
  private final UserConnectionRepository userConnectionRepository;
  private int limitConnections = 1_000;

  public UserConnectionLimitService(
      UserRepository userRepository, UserConnectionRepository userConnectionRepository) {
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
  }
}
