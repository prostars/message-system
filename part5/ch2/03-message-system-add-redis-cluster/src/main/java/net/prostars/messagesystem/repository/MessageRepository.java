package net.prostars.messagesystem.repository;

import java.util.List;
import java.util.Optional;
import net.prostars.messagesystem.dto.projection.MessageInfoProjection;
import net.prostars.messagesystem.entity.ChannelSequenceId;
import net.prostars.messagesystem.entity.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, ChannelSequenceId> {

  @Query("SELECT MAX(m.messageSequence) FROM MessageEntity m WHERE m.channelId = :channelId")
  Optional<Long> findLastMessageSequenceByChannelId(@Param("channelId") Long channelId);

  List<MessageInfoProjection> findByChannelIdAndMessageSequenceBetween(
      @NonNull Long ChannelId,
      @NonNull Long startMessageSequence,
      @NonNull Long endMessageSequence);
}
