package net.prostars.messagesystem.repository;

import java.util.Optional;

import jakarta.persistence.LockModeType;
import net.prostars.messagesystem.dto.domain.InviteCode;
import net.prostars.messagesystem.dto.projection.InviteCodeProjection;
import net.prostars.messagesystem.dto.projection.UsernameProjection;
import net.prostars.messagesystem.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
  
  Optional<UserEntity> findByUsername(@NonNull String username);
  
  Optional<UsernameProjection> findByUserId(@NonNull Long userId);

  Optional<UserEntity> findByConnectionInviteCode(@NonNull String connectionInviteCode);
  
  Optional<InviteCodeProjection> findInviteCodeByUserId(@NonNull Long userId);
  
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  Optional<UserEntity> findForUpdateByUserId(@NonNull Long userId);
}
