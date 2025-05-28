package net.prostars.messagesystem.repository;

import jakarta.persistence.LockModeType;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import net.prostars.messagesystem.dto.projection.CountProjection;
import net.prostars.messagesystem.dto.projection.InviteCodeProjection;
import net.prostars.messagesystem.dto.projection.UserIdProjection;
import net.prostars.messagesystem.dto.projection.UsernameProjection;
import net.prostars.messagesystem.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

  Optional<UserIdProjection> findUserIdByUsername(@NonNull String username);

  List<UserIdProjection> findByUsernameIn(@NonNull Collection<String> usernames);

  Optional<UsernameProjection> findByUserId(@NonNull Long userId);

  Optional<UserEntity> findByInviteCode(@NonNull String inviteCode);

  Optional<InviteCodeProjection> findInviteCodeByUserId(@NonNull Long userId);

  Optional<CountProjection> findCountByUserId(@NonNull Long userId);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  Optional<UserEntity> findForUpdateByUserId(@NonNull Long userId);
}
