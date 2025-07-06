package net.prostars.messagesystem.service;

import java.util.Optional;
import net.prostars.messagesystem.constant.UserConnectionStatus;
import net.prostars.messagesystem.dto.domain.InviteCode;
import net.prostars.messagesystem.dto.domain.User;
import net.prostars.messagesystem.dto.domain.UserId;
import net.prostars.messagesystem.entity.UserConnectionEntity;
import net.prostars.messagesystem.repository.UserConnectionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserConnectionService {

  private static final Logger log = LoggerFactory.getLogger(UserConnectionService.class);

  private final UserService userService;
  private final UserConnectionRepository userConnectionRepository;

  public UserConnectionService(
      UserService userService, UserConnectionRepository userConnectionRepository) {
    this.userService = userService;
    this.userConnectionRepository = userConnectionRepository;
  }

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
          yield Pair.of(Optional.empty(), "InviteRequest failed."); 
        }
      }
      case ACCEPTED -> Pair.of(Optional.empty(), "Already connected with " + partnerUsername);
      case PENDING, REJECTED -> {
        log.info("{} invites {} but does not deliver the invitation request.", inviterUserId, partnerUsername);
        yield Pair.of(Optional.empty(), "Already invited to " + partnerUsername);
      }
    };
  }

  private UserConnectionStatus getStatus(UserId inviterUserId, UserId partnerUserId) {
    return userConnectionRepository
        .findByPartnerAUserIdAndPartnerBUserId(
            Long.min(inviterUserId.id(), partnerUserId.id()),
            Long.max(inviterUserId.id(), partnerUserId.id()))
        .map(status -> UserConnectionStatus.valueOf(status.getStatus()))
        .orElse(UserConnectionStatus.NONE);
  }

  @Transactional
  private void setStatus(
      UserId inviterUserId, UserId partnerUserId, UserConnectionStatus userConnectionStatus) {
    if (userConnectionStatus == UserConnectionStatus.ACCEPTED) {
      throw new IllegalArgumentException("Can't set to accepted.");
    }

    userConnectionRepository.save(
        new UserConnectionEntity(
            Long.min(inviterUserId.id(), partnerUserId.id()),
            Long.max(inviterUserId.id(), partnerUserId.id()),
            userConnectionStatus,
            inviterUserId.id()));
  }
}
