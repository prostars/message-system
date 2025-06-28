package net.prostars.messagesystem.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShardContext {

  private static final Logger log = LoggerFactory.getLogger(ShardContext.class);
  private static final ThreadLocal<Long> threadLocal = new ThreadLocal<>();

  public static Long getChannelId() {
    return threadLocal.get();
  }

  public static void setChannelId(Long channelId) {
    if (channelId == null) {
      throw new IllegalArgumentException("channelID cannot be null.");
    }
    log.info("set shard channelId={}", channelId);
    threadLocal.set(channelId);
  }

  public static void clear() {
    log.info("remove thread local.");
    threadLocal.remove();
  }

  public static final class ShardContextScope implements AutoCloseable {
    public ShardContextScope(Long channelId) {
      ShardContext.setChannelId(channelId);
    }

    @Override
    public void close() {
      ShardContext.clear();
    }
  }
}
