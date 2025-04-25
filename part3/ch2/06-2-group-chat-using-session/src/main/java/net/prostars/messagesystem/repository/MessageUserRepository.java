package net.prostars.messagesystem.repository;

import java.util.Optional;
import net.prostars.messagesystem.entity.MessageUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageUserRepository extends JpaRepository<MessageUserEntity, Long> {
  
  Optional<MessageUserEntity> findByUsername(@NonNull String username);
}
