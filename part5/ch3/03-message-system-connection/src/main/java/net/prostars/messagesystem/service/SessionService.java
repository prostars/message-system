package net.prostars.messagesystem.service;

import java.time.Instant;
import java.util.*;
import net.prostars.messagesystem.constant.IdKey;
import net.prostars.messagesystem.constant.KeyPrefix;
import net.prostars.messagesystem.dto.domain.UserId;
import net.prostars.messagesystem.kafka.ListenTopicCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.stereotype.Service;

@Service
public class SessionService {

  private static final Logger log = LoggerFactory.getLogger(SessionService.class);

  private final SessionRepository<? extends Session> httpSessionRepository;
  private final CacheService cacheService;
  private final ListenTopicCreator listenTopicCreator;
  private final long TTL = 300;

  public SessionService(
      SessionRepository<? extends Session> httpSessionRepository,
      CacheService cacheService,
      ListenTopicCreator listenTopicCreator) {
    this.httpSessionRepository = httpSessionRepository;
    this.cacheService = cacheService;
    this.listenTopicCreator = listenTopicCreator;
  }

  public void setOnline(UserId userId, boolean status) {
    String key = buildUserLocationKey(userId);
    if (status) {
      cacheService.set(key, listenTopicCreator.getListenTopic(), TTL);
    } else {
      cacheService.delete(key);
    }
  }

  public boolean removeActiveChannel(UserId userId) {
    return cacheService.delete(buildChannelIdKey(userId));
  }

  public void refreshTTL(UserId userId, String httpSessionId) {
    try {
      Session httpSession = httpSessionRepository.findById(httpSessionId);
      if (httpSession != null) {
        httpSession.setLastAccessedTime(Instant.now());
        cacheService.expire(buildChannelIdKey(userId), TTL);
        cacheService.expire(buildUserLocationKey(userId), TTL);
      }
    } catch (Exception ex) {
      log.error("Redis find failed. httpSessionId: {}, cause: {}", httpSessionId, ex.getMessage());
    }
  }

  private String buildChannelIdKey(UserId userId) {
    return cacheService.buildKey(
        KeyPrefix.USER, userId.id().toString(), IdKey.CHANNEL_ID.getValue());
  }

  private String buildUserLocationKey(UserId userId) {
    return cacheService.buildKey(KeyPrefix.USER_SESSION, userId.id().toString());
  }
}
