package net.prostars.messagesystem.repository;

import net.prostars.messagesystem.entity.UserChannelId;
import net.prostars.messagesystem.entity.UserChannelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface UserChannelRepository extends JpaRepository<UserChannelEntity, UserChannelId> {

  boolean existsByUserIdAndChannelId(@NonNull Long UserId, @NonNull Long ChannelId);
}
