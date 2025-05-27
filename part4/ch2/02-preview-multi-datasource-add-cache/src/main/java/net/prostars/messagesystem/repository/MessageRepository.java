package net.prostars.messagesystem.repository;

import java.util.Optional;
import net.prostars.messagesystem.entity.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, Long> {

  @Transactional(readOnly = true)
  Optional<MessageEntity> findTopByOrderByMessageSequenceDesc();
}
