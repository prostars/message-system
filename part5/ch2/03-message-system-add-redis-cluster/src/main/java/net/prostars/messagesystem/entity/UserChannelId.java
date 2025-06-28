package net.prostars.messagesystem.entity;

import java.io.Serializable;
import java.util.Objects;

public class UserChannelId implements Serializable {

  private Long userId;
  private Long channelId;

  public UserChannelId() {}

  public UserChannelId(Long userId, Long channelId) {
    this.userId = userId;
    this.channelId = channelId;
  }

  public Long getUserId() {
    return userId;
  }

  public Long getChannelId() {
    return channelId;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    UserChannelId that = (UserChannelId) o;
    return Objects.equals(userId, that.userId) && Objects.equals(channelId, that.channelId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userId, channelId);
  }

  @Override
  public String toString() {
    return "ChannelId{UserId=%d, ChannelId=%d}".formatted(userId, channelId);
  }
}
