package net.prostars.messagesystem.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import net.prostars.messagesystem.constant.UserConnectionStatus;
import net.prostars.messagesystem.dto.projection.InviterUserIdProjection;
import net.prostars.messagesystem.dto.projection.UserConnectionStatusProjection;
import net.prostars.messagesystem.dto.projection.UserIdUsernameInviterUserIdProjection;
import net.prostars.messagesystem.entity.UserConnectionEntity;
import net.prostars.messagesystem.entity.UserConnectionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface UserConnectionRepository
    extends JpaRepository<UserConnectionEntity, UserConnectionId> {

  Optional<UserConnectionStatusProjection>
      findUserConnectionStatusByPartnerAUserIdAndPartnerBUserId(
          @NonNull Long partnerAUserId, @NonNull Long partnerBUserId);

  Optional<UserConnectionEntity> findByPartnerAUserIdAndPartnerBUserIdAndStatus(
      @NonNull Long partnerAUserId,
      @NonNull Long partnerBUserId,
      @NonNull UserConnectionStatus status);

  Optional<InviterUserIdProjection> findInviterUserIdByPartnerAUserIdAndPartnerBUserId(
      @NonNull Long partnerAUserId, @NonNull Long partnerBUserId);

  long countByPartnerAUserIdAndPartnerBUserIdInAndStatus(
      @NonNull Long partnerAUserId,
      @NonNull Collection<Long> partnerBUserIds,
      @NonNull UserConnectionStatus status);

  long countByPartnerBUserIdAndPartnerAUserIdInAndStatus(
          @NonNull Long partnerBUserId,
          @NonNull Collection<Long> partnerAUserIds,
          @NonNull UserConnectionStatus status);

  @Query(
      "SELECT u.partnerBUserId AS userId, userB.username AS username, u.inviterUserId AS inviterUserId "
          + "FROM UserConnectionEntity u "
          + "INNER JOIN UserEntity userB ON u.partnerBUserId = userB.userId "
          + "WHERE u.partnerAUserId = :userId AND u.status = :status")
  List<UserIdUsernameInviterUserIdProjection> findByPartnerAUserIdAndStatus(
      @NonNull @Param("userId") Long userId, @NonNull @Param("status") UserConnectionStatus status);

  @Query(
      "SELECT u.partnerAUserId AS userId, userA.username AS username, u.inviterUserId AS inviterUserId "
          + "FROM UserConnectionEntity u "
          + "INNER JOIN UserEntity userA ON u.partnerAUserId = userA.userId "
          + "WHERE u.partnerBUserId = :userId AND u.status = :status")
  List<UserIdUsernameInviterUserIdProjection> findByPartnerBUserIdAndStatus(
      @NonNull @Param("userId") Long userId, @NonNull @Param("status") UserConnectionStatus status);
}
