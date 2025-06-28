package net.prostars.messagesystem.service;

import java.util.*;
import net.prostars.messagesystem.constant.IdKey;
import net.prostars.messagesystem.constant.KeyPrefix;
import net.prostars.messagesystem.dto.domain.ChannelId;
import net.prostars.messagesystem.dto.domain.UserId;
import org.springframework.stereotype.Service;

@Service
public class SessionService {

  private final CacheService cacheService;
  private final long TTL = 300;

  public SessionService(CacheService cacheService) {
    this.cacheService = cacheService;
  }

  public Optional<String> getListenTopic(UserId userId) {
    return cacheService.get(buildUserLocationKey(userId));
  }

  public Map<String, List<UserId>> getListenTopics(Collection<UserId> userIds) {
    List<String> keys = userIds.stream().map(this::buildUserLocationKey).toList();
    List<String> listenTopics = cacheService.get(keys);
    Map<String, List<UserId>> locationToUsers = new HashMap<>();
    Iterator<String> iterator = listenTopics.iterator();

    for (UserId userId : userIds) {
      String listenTopic = iterator.next();
      if (listenTopic != null) {
        locationToUsers.computeIfAbsent(listenTopic, unused -> new ArrayList<>()).add(userId);
      }
    }
    return locationToUsers;
  }

  public List<UserId> getOnlineParticipantUserIds(ChannelId channelId, List<UserId> userIds) {
    List<String> channelIdKeys = userIds.stream().map(this::buildChannelIdKey).toList();
    List<String> channelIds = cacheService.get(channelIdKeys);
    if (channelIds != null) {
      List<UserId> onlineParticipantUserIds = new ArrayList<>(userIds.size());
      String chId = channelId.id().toString();
      for (int idx = 0; idx < userIds.size(); idx++) {
        String value = channelIds.get(idx);
        if (value != null && value.equals(chId)) {
          onlineParticipantUserIds.add(userIds.get(idx));
        }
      }
      return onlineParticipantUserIds;
    }
    return Collections.emptyList();
  }

  public boolean setActiveChannel(UserId userId, ChannelId channelId) {
    return cacheService.set(buildChannelIdKey(userId), channelId.id().toString(), TTL);
  }

  public boolean removeActiveChannel(UserId userId) {
    return cacheService.delete(buildChannelIdKey(userId));
  }

  private String buildChannelIdKey(UserId userId) {
    return cacheService.buildKey(
        KeyPrefix.USER, userId.id().toString(), IdKey.CHANNEL_ID.getValue());
  }

  private String buildUserLocationKey(UserId userId) {
    return cacheService.buildKey(KeyPrefix.USER_SESSION, userId.id().toString());
  }
}
