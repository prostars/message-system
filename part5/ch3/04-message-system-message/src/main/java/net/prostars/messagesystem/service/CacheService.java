package net.prostars.messagesystem.service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
public class CacheService {

  private static final Logger log = LoggerFactory.getLogger(CacheService.class);

  private final StringRedisTemplate stringRedisTemplate;

  public CacheService(StringRedisTemplate stringRedisTemplate) {
    this.stringRedisTemplate = stringRedisTemplate;
  }

  public Optional<String> get(String key) {
    try {
      String value = stringRedisTemplate.opsForValue().get(key);
      if (value != null) {
        return Optional.of(value);
      }
    } catch (Exception ex) {
      log.error("Redis get failed. key: {}, cause: {}", key, ex.getMessage());
    }
    return Optional.empty();
  }

  public List<String> get(Collection<String> keys) {
    try {
      return stringRedisTemplate.opsForValue().multiGet(keys);
    } catch (Exception ex) {
      log.error("Redis mget failed. keys: {}, cause: {}", keys, ex.getMessage());
    }
    return Collections.emptyList();
  }

  public boolean set(String key, String value, Long ttlSeconds) {
    try {
      stringRedisTemplate.opsForValue().set(key, value, ttlSeconds, TimeUnit.SECONDS);
      return true;
    } catch (Exception ex) {
      log.error("Redis set failed. key: {}, cause: {}", key, ex.getMessage());
    }
    return false;
  }

  public boolean set(Map<String, String> map, Long ttlSeconds) {
    try {
      stringRedisTemplate.executePipelined(
          new SessionCallback<>() {
            @Override
            @SuppressWarnings("unchecked")
            public <K, V> Object execute(@NonNull RedisOperations<K, V> operations)
                throws DataAccessException {
              map.forEach(
                  (key, value) -> operations.opsForValue().set((K) key, (V) value, ttlSeconds));
              return null;
            }
          });
      return true;
    } catch (Exception ex) {
      log.error("Redis multiset failed. keys: {}, cause: {}", map.keySet(), ex.getMessage());
    }
    return false;
  }

  public boolean expire(String key, Long ttlSeconds) {
    try {
      stringRedisTemplate.expire(key, ttlSeconds, TimeUnit.SECONDS);
      return true;
    } catch (Exception ex) {
      log.error("Redis expire failed. key: {}, cause: {}", key, ex.getMessage());
    }
    return false;
  }

  public boolean delete(String key) {
    try {
      stringRedisTemplate.delete(key);
      return true;
    } catch (Exception ex) {
      log.error("Redis delete failed. key: {}, cause: {}", key, ex.getMessage());
    }
    return false;
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

  public String buildKey(String prefix, String firstKey, String secondKey) {
    return "%s:%s:%s".formatted(prefix, firstKey, secondKey);
  }
}
