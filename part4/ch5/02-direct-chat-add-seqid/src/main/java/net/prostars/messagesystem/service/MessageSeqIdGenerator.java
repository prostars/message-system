package net.prostars.messagesystem.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class MessageSeqIdGenerator {

  private final StringRedisTemplate stringRedisTemplate;

  public MessageSeqIdGenerator(StringRedisTemplate stringRedisTemplate) {
    this.stringRedisTemplate = stringRedisTemplate;
  }

  public Long getNext(Long channelId) {
    return stringRedisTemplate.opsForValue().increment(buildMessageSeqIdKey(channelId));
  }

  public Long peek(Long channelId) {
    String value = stringRedisTemplate.opsForValue().get(buildMessageSeqIdKey(channelId));
    if (value != null) {
      return Long.valueOf(value);
    }
    return 0L;
  }

  private String buildMessageSeqIdKey(Long channelId) {
    return "message:channel:%d:seq_id".formatted(channelId);
  }
}
