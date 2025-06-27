package net.prostars.messagesystem.repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import net.prostars.messagesystem.dto.projection.ChannelProjection;
import net.prostars.messagesystem.dto.projection.ChannelTitleProjection;
import net.prostars.messagesystem.dto.projection.InviteCodeProjection;
import net.prostars.messagesystem.entity.ChannelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface ChannelRepository extends JpaRepository<ChannelEntity, Long> {

  Optional<ChannelTitleProjection> findChannelTitleByChannelId(@NonNull Long channelId);

  Optional<InviteCodeProjection> findChannelInviteCodeByChannelId(@NonNull Long channelId);

  Optional<ChannelProjection> findChannelByInviteCode(@NonNull String inviteCode);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  Optional<ChannelEntity> findForUpdateByChannelId(@NonNull Long channelId);
}
