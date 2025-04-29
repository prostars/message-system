package net.prostars.messagesystem.service;

import java.util.List;
import java.util.Optional;
import net.prostars.messagesystem.constant.ResultType;
import net.prostars.messagesystem.dto.domain.Channel;
import net.prostars.messagesystem.dto.domain.ChannelId;
import net.prostars.messagesystem.dto.domain.UserId;
import net.prostars.messagesystem.dto.projection.ChannelTitleProjection;
import net.prostars.messagesystem.entity.ChannelEntity;
import net.prostars.messagesystem.entity.UserChannelEntity;
import net.prostars.messagesystem.repository.ChannelRepository;
import net.prostars.messagesystem.repository.UserChannelRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.transaction.annotation.Transactional;

public class ChannelService {

  private static final Logger log = LoggerFactory.getLogger(ChannelService.class);

  private final SessionService sessionService;
  private final ChannelRepository channelRepository;
  private final UserChannelRepository userChannelRepository;

  public ChannelService(
      SessionService sessionService,
      ChannelRepository channelRepository,
      UserChannelRepository userChannelRepository) {
    this.sessionService = sessionService;
    this.channelRepository = channelRepository;
    this.userChannelRepository = userChannelRepository;
  }

  public boolean isJoined(ChannelId channelId, UserId userId) {
    return userChannelRepository.existsByUserIdAndChannelId(userId.id(), channelId.id());
  }

  @Transactional
  public Pair<Optional<Channel>, ResultType> create(
      UserId senderUserId, UserId participantId, String title) {
    if (title == null || title.isEmpty()) {
      log.warn("Invalid args : title is empty.");
      return Pair.of(Optional.empty(), ResultType.INVALID_ARGS);
    }

    try {
      final int HEAD_COUNT = 2;
      ChannelEntity channelEntity = channelRepository.save(new ChannelEntity(title, HEAD_COUNT));
      Long channelId = channelEntity.getChannelId();
      List<UserChannelEntity> userChannelEntities =
          List.of(
              new UserChannelEntity(senderUserId.id(), channelId, 0),
              new UserChannelEntity(participantId.id(), channelId, 0));
      userChannelRepository.saveAll(userChannelEntities);
      Channel channel = new Channel(new ChannelId(channelId), title, HEAD_COUNT);
      return Pair.of(Optional.of(channel), ResultType.SUCCESS);
    } catch (Exception ex) {
      log.error("Create failed. cause: {}", ex.getMessage());
      throw ex;
    }
  }

  public Pair<Optional<String>, ResultType> enter(ChannelId channelId, UserId userId) {
    if (!isJoined(channelId, userId)) {
      log.warn(
          "Enter channel failed. User not joined the channel. channelId: {}, userId: {}",
          channelId,
          userId);
      return Pair.of(Optional.empty(), ResultType.NOT_JOINED);
    }

    Optional<String> title =
        channelRepository
            .findChannelTitleByChannelId(channelId.id())
            .map(ChannelTitleProjection::getTitle);
    if (title.isEmpty()) {
      log.warn(
          "Enter channel failed. Channel does not exist. channelId: {}, userId: {}",
          channelId,
          userId);
      return Pair.of(Optional.empty(), ResultType.NOT_FOUND);
    }

    if (sessionService.setActiveChannel(userId, channelId)) {
      return Pair.of(title, ResultType.SUCCESS);
    }

    log.error("Enter channel failed. channelId: {}, userId: {}", channelId, userId);
    return Pair.of(Optional.empty(), ResultType.FAILED);
  }
}
