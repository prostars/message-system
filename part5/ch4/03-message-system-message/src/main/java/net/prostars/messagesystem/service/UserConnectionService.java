package net.prostars.messagesystem.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.prostars.messagesystem.constant.KeyPrefix;
import net.prostars.messagesystem.constant.UserConnectionStatus;
import net.prostars.messagesystem.dto.domain.InviteCode;
import net.prostars.messagesystem.dto.domain.User;
import net.prostars.messagesystem.dto.domain.UserId;
import net.prostars.messagesystem.dto.projection.UserIdUsernameInviterUserIdProjection;
import net.prostars.messagesystem.entity.UserConnectionEntity;
import net.prostars.messagesystem.json.JsonUtil;
import net.prostars.messagesystem.repository.UserConnectionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
public class UserConnectionService {

  private static final Logger log = LoggerFactory.getLogger(UserConnectionService.class);

  private final UserService userService;
  private final UserConnectionLimitService userConnectionLimitService;
  private final CacheService cacheService;
  private final UserConnectionRepository userConnectionRepository;
  private final JsonUtil jsonUtil;
  private final long TTL = 600;

  public UserConnectionService(
      UserService userService,
      UserConnectionLimitService userConnectionLimitService,
      CacheService cacheService,
      UserConnectionRepository userConnectionRepository,
      JsonUtil jsonUtil) {
    this.userService = userService;
    this.userConnectionLimitService = userConnectionLimitService;
    this.cacheService = cacheService;
    this.userConnectionRepository = userConnectionRepository;
    this.jsonUtil = jsonUtil;
  }

  @Transactional(readOnly = true)
  public List<User> getUsersByStatus(UserId userId, UserConnectionStatus status) {
    String key =
        cacheService.buildKey(KeyPrefix.CONNECTIONS_STATUS, userId.id().toString(), status.name());
    Optional<String> cachedUsers = cacheService.get(key);
    if (cachedUsers.isPresent()) {
      return jsonUtil.fromJsonToList(cachedUsers.get(), User.class);
    }

    List<UserIdUsernameInviterUserIdProjection> usersA =
        userConnectionRepository.findByPartnerAUserIdAndStatus(userId.id(), status);
    List<UserIdUsernameInviterUserIdProjection> usersB =
        userConnectionRepository.findByPartnerBUserIdAndStatus(userId.id(), status);
    List<User> fromDb;
    if (status == UserConnectionStatus.ACCEPTED) {
      fromDb =
          Stream.concat(usersA.stream(), usersB.stream())
              .map(item -> new User(new UserId(item.getUserId()), item.getUsername()))
              .toList();
    } else {
      fromDb =
          Stream.concat(usersA.stream(), usersB.stream())
              .filter(item -> !item.getInviterUserId().equals(userId.id()))
              .map(item -> new User(new UserId(item.getUserId()), item.getUsername()))
              .toList();
    }
    if (!fromDb.isEmpty()) {
      jsonUtil.toJson(fromDb).ifPresent(json -> cacheService.set(key, json, TTL));
    }
    return fromDb;
  }

  @Transactional(readOnly = true)
  public long countConnectionStatus(
      UserId senderUserId, List<UserId> partnerUserIds, UserConnectionStatus status) {
    List<Long> ids = partnerUserIds.stream().map(UserId::id).toList();
    return userConnectionRepository.countByPartnerAUserIdAndPartnerBUserIdInAndStatus(
            senderUserId.id(), ids, status)
        + userConnectionRepository.countByPartnerBUserIdAndPartnerAUserIdInAndStatus(
            senderUserId.id(), ids, status);
  }

  @Transactional
  public Pair<Optional<UserId>, String> invite(UserId inviterUserId, InviteCode inviteCode) {
    Optional<User> partner = userService.getUser(inviteCode);
    if (partner.isEmpty()) {
      log.info("Invalid invite code. {}, from {}", inviteCode, inviterUserId);
      return Pair.of(Optional.empty(), "Invalid invite code.");
    }

    UserId partnerUserId = partner.get().userId();
    String partnerUsername = partner.get().username();
    if (partnerUserId.equals(inviterUserId)) {
      return Pair.of(Optional.empty(), "Can't self invite.");
    }

    UserConnectionStatus userConnectionStatus = getStatus(inviterUserId, partnerUserId);
    return switch (userConnectionStatus) {
      case NONE, DISCONNECTED -> {
        if (userService
            .getConnectionCount(inviterUserId)
            .filter(count -> count >= userConnectionLimitService.getLimitConnections())
            .isPresent()) {
          yield Pair.of(Optional.empty(), "Connection limit reached.");
        }

        Optional<String> inviterUsername = userService.getUsername(inviterUserId);
        if (inviterUsername.isEmpty()) {
          log.warn("InviteRequest failed.");
          yield Pair.of(Optional.empty(), "InviteRequest failed.");
        }
        try {
          setStatus(inviterUserId, partnerUserId, UserConnectionStatus.PENDING);
          yield Pair.of(Optional.of(partnerUserId), inviterUsername.get());
        } catch (Exception ex) {
          log.error("Set pending failed. cause: {}", ex.getMessage());
          TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
          yield Pair.of(Optional.empty(), "InviteRequest failed.");
        }
      }
      case ACCEPTED ->
          Pair.of(Optional.empty(), "Already connected with " + partnerUsername);
      case PENDING, REJECTED -> {
        log.info(
            "{} invites {} but does not deliver the invitation request.",
            inviterUserId,
            partnerUsername);
        yield Pair.of(Optional.empty(), "Already invited to " + partnerUsername);
      }
    };
  }

  @Transactional
  public Pair<Optional<UserId>, String> accept(UserId acceptorUserId, String inviterUsername) {
    Optional<UserId> userId = userService.getUserId(inviterUsername);
    if (userId.isEmpty()) {
      return Pair.of(Optional.empty(), "Invalid username.");
    }
    UserId inviterUserId = userId.get();

    if (acceptorUserId.equals(inviterUserId)) {
      return Pair.of(Optional.empty(), "Can't self accept.");
    }

    if (getInviterUserId(acceptorUserId, inviterUserId)
        .filter(invitationSenderUserId -> invitationSenderUserId.equals(inviterUserId))
        .isEmpty()) {
      return Pair.of(Optional.empty(), "Invalid username.");
    }

    UserConnectionStatus userConnectionStatus = getStatus(inviterUserId, acceptorUserId);
    if (userConnectionStatus == UserConnectionStatus.ACCEPTED) {
      return Pair.of(Optional.empty(), "Already connected.");
    }
    if (userConnectionStatus != UserConnectionStatus.PENDING) {
      return Pair.of(Optional.empty(), "Accept failed.");
    }

    Optional<String> acceptorUsername = userService.getUsername(acceptorUserId);
    if (acceptorUsername.isEmpty()) {
      log.error("Invalid userId. userId: {}", acceptorUserId);
      return Pair.of(Optional.empty(), "Accept failed.");
    }

    try {
      userConnectionLimitService.accept(acceptorUserId, inviterUserId);
      return Pair.of(Optional.of(inviterUserId), acceptorUsername.get());
    } catch (IllegalStateException ex) {
      if (TransactionSynchronizationManager.isActualTransactionActive()) {
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      }
      return Pair.of(Optional.empty(), ex.getMessage());
    } catch (Exception ex) {
      if (TransactionSynchronizationManager.isActualTransactionActive()) {
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      }
      log.error("Accept failed. cause: {}", ex.getMessage());
      return Pair.of(Optional.empty(), "Accept failed.");
    }
  }

  @Transactional
  public Pair<Boolean, String> reject(UserId senderUserId, String inviterUsername) {
    return userService
        .getUserId(inviterUsername)
        .filter(inviterUserId -> !inviterUserId.equals(senderUserId))
        .filter(
            inviterUserId ->
                getInviterUserId(inviterUserId, senderUserId)
                    .filter(invitationSenderUserId -> invitationSenderUserId.equals(inviterUserId))
                    .isPresent())
        .filter(
            inviterUserId -> getStatus(inviterUserId, senderUserId) == UserConnectionStatus.PENDING)
        .map(
            inviterUserId -> {
              try {
                setStatus(inviterUserId, senderUserId, UserConnectionStatus.REJECTED);
                return Pair.of(true, inviterUsername);
              } catch (Exception ex) {
                log.error("Set rejected failed. cause: {}", ex.getMessage());
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return Pair.of(false, "Reject failed.");
              }
            })
        .orElse(Pair.of(false, "Reject failed."));
  }

  @Transactional
  public Pair<Boolean, String> disconnect(UserId senderUserId, String partnerUsername) {
    return userService
        .getUserId(partnerUsername)
        .filter(partnerUserId -> !senderUserId.equals(partnerUserId))
        .map(
            partnerUserId -> {
              try {
                UserConnectionStatus userConnectionStatus = getStatus(senderUserId, partnerUserId);
                if (userConnectionStatus == UserConnectionStatus.ACCEPTED) {
                  userConnectionLimitService.disconnect(senderUserId, partnerUserId);
                  return Pair.of(true, partnerUsername);
                } else if (userConnectionStatus == UserConnectionStatus.REJECTED
                    && getInviterUserId(senderUserId, partnerUserId)
                        .filter(inviterUserId -> inviterUserId.equals(partnerUserId))
                        .isPresent()) {
                  setStatus(senderUserId, partnerUserId, UserConnectionStatus.DISCONNECTED);
                  return Pair.of(true, partnerUsername);
                }
              } catch (Exception ex) {
                if (TransactionSynchronizationManager.isActualTransactionActive()) {
                  TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                }
                log.error("Disconnect failed. cause: {}", ex.getMessage());
              }
              return Pair.of(false, "Disconnect failed.");
            })
        .orElse(Pair.of(false, "Disconnect failed."));
  }

  @Transactional(readOnly = true)
  private Optional<UserId> getInviterUserId(UserId partnerAUserId, UserId partnerBUserId) {
    long partnerA = Long.min(partnerAUserId.id(), partnerBUserId.id());
    long partnerB = Long.max(partnerAUserId.id(), partnerBUserId.id());
    String key =
        cacheService.buildKey(
            KeyPrefix.INVITER_USER_ID, String.valueOf(partnerA), String.valueOf(partnerB));
    Optional<String> cachedInviterUserId = cacheService.get(key);
    if (cachedInviterUserId.isPresent()) {
      return Optional.of(new UserId(Long.valueOf(cachedInviterUserId.get())));
    }

    Optional<UserId> fromDb =
        userConnectionRepository
            .findInviterUserIdByPartnerAUserIdAndPartnerBUserId(partnerA, partnerB)
            .map(inviterUserId -> new UserId(inviterUserId.getInviterUserId()));
    fromDb.ifPresent(userId -> cacheService.set(key, userId.id().toString(), TTL));
    return fromDb;
  }

  @Transactional(readOnly = true)
  private UserConnectionStatus getStatus(UserId inviterUserId, UserId partnerUserId) {
    long partnerA = Long.min(inviterUserId.id(), partnerUserId.id());
    long partnerB = Long.max(inviterUserId.id(), partnerUserId.id());
    String key =
        cacheService.buildKey(
            KeyPrefix.CONNECTION_STATUS, String.valueOf(partnerA), String.valueOf(partnerB));
    Optional<String> cachedConnectionStatus = cacheService.get(key);
    if (cachedConnectionStatus.isPresent()) {
      return UserConnectionStatus.valueOf(cachedConnectionStatus.get());
    }

    UserConnectionStatus fromDb =
        userConnectionRepository
            .findUserConnectionStatusByPartnerAUserIdAndPartnerBUserId(partnerA, partnerB)
            .map(status -> UserConnectionStatus.valueOf(status.getStatus()))
            .orElse(UserConnectionStatus.NONE);
    cacheService.set(key, fromDb.name(), TTL);
    return fromDb;
  }

  @Transactional
  private void setStatus(
      UserId inviterUserId, UserId partnerUserId, UserConnectionStatus userConnectionStatus) {
    if (userConnectionStatus == UserConnectionStatus.ACCEPTED) {
      throw new IllegalArgumentException("Can't set to accepted.");
    }

    long partnerA = Long.min(inviterUserId.id(), partnerUserId.id());
    long partnerB = Long.max(inviterUserId.id(), partnerUserId.id());
    userConnectionRepository.save(
        new UserConnectionEntity(partnerA, partnerB, userConnectionStatus, inviterUserId.id()));
    cacheService.delete(
        List.of(
            cacheService.buildKey(
                KeyPrefix.CONNECTION_STATUS, String.valueOf(partnerA), String.valueOf(partnerB)),
            cacheService.buildKey(
                KeyPrefix.CONNECTIONS_STATUS,
                inviterUserId.id().toString(),
                userConnectionStatus.name()),
            cacheService.buildKey(
                KeyPrefix.CONNECTIONS_STATUS,
                partnerUserId.id().toString(),
                userConnectionStatus.name())));
  }
}
