package net.prostars.messagesystem.repository;

import java.util.Optional;
import net.prostars.messagesystem.dto.projection.ChannelTitleProjection;
import net.prostars.messagesystem.entity.ChannelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface ChannelRepository extends JpaRepository<ChannelEntity, Long> {
    
    Optional<ChannelTitleProjection> findChannelTitleByChannelId(@NonNull Long channelId);
}
