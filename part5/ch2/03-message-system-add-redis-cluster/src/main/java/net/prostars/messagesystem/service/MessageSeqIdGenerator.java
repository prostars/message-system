package net.prostars.messagesystem.service;

import java.util.Optional;
import net.prostars.messagesystem.constant.KeyPrefix;
import net.prostars.messagesystem.dto.domain.ChannelId;
import net.prostars.messagesystem.dto.domain.MessageSeqId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class MessageSeqIdGenerator {

  private static final Logger log = LoggerFactory.getLogger(MessageSeqIdGenerator.class);

  private final StringRedisTemplate stringRedisTemplate;

  public MessageSeqIdGenerator(StringRedisTemplate stringRedisTemplate) {
    this.stringRedisTemplate = stringRedisTemplate;
  }

  public Optional<MessageSeqId> getNext(ChannelId channelId) {
    String seqIdKey = buildMessageSeqIdKey(channelId.id());
    try {
      return Optional.of(new MessageSeqId(stringRedisTemplate.opsForValue().increment(seqIdKey)));
    } catch (Exception ex) {
      log.error("Redis set failed. key: {}, cause: {}", seqIdKey, ex.getMessage());
    }
    return Optional.empty();
  }

  private String buildMessageSeqIdKey(Long channelId) {
    return "%s:%d:seq_id".formatted(KeyPrefix.CHANNEL, channelId);
  }
}
