package net.prostars.messagesystem.dto.domain;

import java.util.Objects;

public record Channel(ChannelId channelId, String title, int headCount) {

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    Channel channel = (Channel) o;
    return Objects.equals(channelId, channel.channelId);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(channelId);
  }
}
