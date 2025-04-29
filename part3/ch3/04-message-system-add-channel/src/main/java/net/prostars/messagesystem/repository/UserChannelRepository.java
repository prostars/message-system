package net.prostars.messagesystem.repository;

import java.util.List;
import net.prostars.messagesystem.dto.domain.ChannelId;
import net.prostars.messagesystem.dto.projection.UserIdProjection;
import net.prostars.messagesystem.entity.UserChannelEntity;
import net.prostars.messagesystem.entity.UserChannelId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface UserChannelRepository extends JpaRepository<UserChannelEntity, UserChannelId> {

  boolean existsByUserIdAndChannelId(@NonNull Long UserId, @NonNull Long ChannelId);

  List<UserIdProjection> findUserIdsByChannelId(@NonNull Long channelId);
}
