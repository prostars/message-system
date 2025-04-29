package net.prostars.messagesystem.repository;

import java.util.List;

import net.prostars.messagesystem.dto.projection.ChannelProjection;
import net.prostars.messagesystem.dto.projection.UserIdProjection;
import net.prostars.messagesystem.entity.UserChannelEntity;
import net.prostars.messagesystem.entity.UserChannelId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface UserChannelRepository extends JpaRepository<UserChannelEntity, UserChannelId> {

  boolean existsByUserIdAndChannelId(@NonNull Long UserId, @NonNull Long ChannelId);

  List<UserIdProjection> findUserIdsByChannelId(@NonNull Long channelId);

  @Query(
      "SELECT c.channelId AS channelId, c.title AS title, c.headCount AS headCount "
          + "FROM UserChannelEntity uc "
          + "INNER JOIN ChannelEntity c ON uc.channelId = c.channelId "
          + "WHERE uc.userId = :userId")
  List<ChannelProjection> findChannelsByUserId(@NonNull @Param("userId") Long userId);
  
  void deleteByUserIdAndChannelId(@NonNull Long userId, @NonNull Long channelId);
}
