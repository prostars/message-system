package net.prostars.messagesystem.service;

import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class CacheService {

  private static final Logger log = LoggerFactory.getLogger(CacheService.class);

  private final StringRedisTemplate stringRedisTemplate;

  public CacheService(StringRedisTemplate stringRedisTemplate) {
    this.stringRedisTemplate = stringRedisTemplate;
  }

  public boolean delete(Collection<String> keys) {
    try {
      stringRedisTemplate.delete(keys);
      return true;
    } catch (Exception ex) {
      log.error("Redis multi delete failed. keys: {}, cause: {}", keys, ex.getMessage());
    }
    return false;
  }

  public String buildKey(String prefix, String key) {
    return "%s:%s".formatted(prefix, key);
  }
}
