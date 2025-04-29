package net.prostars.messagesystem.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import net.prostars.messagesystem.constant.ResultType;
import net.prostars.messagesystem.constant.UserConnectionStatus;
import net.prostars.messagesystem.dto.domain.Channel;
import net.prostars.messagesystem.dto.domain.ChannelId;
import net.prostars.messagesystem.dto.domain.InviteCode;
import net.prostars.messagesystem.dto.domain.UserId;
import net.prostars.messagesystem.dto.projection.ChannelTitleProjection;
import net.prostars.messagesystem.entity.ChannelEntity;
import net.prostars.messagesystem.entity.UserChannelEntity;
import net.prostars.messagesystem.repository.ChannelRepository;
import net.prostars.messagesystem.repository.UserChannelRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChannelService {

  private static final Logger log = LoggerFactory.getLogger(ChannelService.class);
  private static final int LIMIT_HEAD_COUNT = 100;

  private final SessionService sessionService;
  private final UserConnectionService userConnectionService;
  private final ChannelRepository channelRepository;
  private final UserChannelRepository userChannelRepository;

  public ChannelService(
      SessionService sessionService,
      UserConnectionService userConnectionService,
      ChannelRepository channelRepository,
      UserChannelRepository userChannelRepository) {
    this.sessionService = sessionService;
    this.userConnectionService = userConnectionService;
    this.channelRepository = channelRepository;
    this.userChannelRepository = userChannelRepository;
  }

  public Optional<InviteCode> getInviteCode(ChannelId channelId) {
    Optional<InviteCode> inviteCode = channelRepository
        .findChannelInviteCodeByChannelId(channelId.id())
        .map(projection -> new InviteCode(projection.getInviteCode()));
    if (inviteCode.isEmpty()) {
      log.warn("Invite code is not exist. channelId: {}", channelId);
    }
    return inviteCode;
  }

  public boolean isJoined(ChannelId channelId, UserId userId) {
    return userChannelRepository.existsByUserIdAndChannelId(userId.id(), channelId.id());
  }

  public List<UserId> getParticipantIds(ChannelId channelId) {
    return userChannelRepository.findUserIdsByChannelId(channelId.id()).stream()
        .map(userId -> new UserId(userId.getUserId()))
        .toList();
  }

  public List<UserId> getOnlineParticipantIds(ChannelId channelId) {
    return sessionService.getOnlineParticipantUserIds(channelId, getParticipantIds(channelId));
  }

  @Transactional
  public Pair<Optional<Channel>, ResultType> create(
      UserId senderUserId, List<UserId> participantIds, String title) {
    if (title == null || title.isEmpty()) {
      log.warn("Invalid args : title is empty.");
      return Pair.of(Optional.empty(), ResultType.INVALID_ARGS);
    }

    int headCount = participantIds.size() + 1;
    if (headCount > LIMIT_HEAD_COUNT) {
      log.warn(
          "Over limit of channel. senderUserId: {}, participantIds count={}, title={}",
          senderUserId,
          participantIds.size(),
          title);
      return Pair.of(Optional.empty(), ResultType.OVER_LIMIT);
    }

    if (userConnectionService.countConnectionStatus(
            senderUserId, participantIds, UserConnectionStatus.ACCEPTED)
        != participantIds.size()) {
      log.warn("Included unconnected user. participantIds: {}", participantIds);
      return Pair.of(Optional.empty(), ResultType.NOT_ALLOWED);
    }

    try {
      ChannelEntity channelEntity = channelRepository.save(new ChannelEntity(title, headCount));
      Long channelId = channelEntity.getChannelId();
      List<UserChannelEntity> userChannelEntities =
          participantIds.stream()
              .map(participantId -> new UserChannelEntity(participantId.id(), channelId, 0))
              .collect(Collectors.toList());
      userChannelEntities.add(new UserChannelEntity(senderUserId.id(), channelId, 0));
      userChannelRepository.saveAll(userChannelEntities);
      Channel channel = new Channel(new ChannelId(channelId), title, headCount);
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
