package net.prostars.messagesystem.entity;

import java.io.Serializable;
import java.util.Objects;

public class ChannelSequenceId implements Serializable {

  private Long channelId;
  private Long messageSequence;

  public ChannelSequenceId() {}

  public ChannelSequenceId(Long channelId, Long messageSequence) {
    this.channelId = channelId;
    this.messageSequence = messageSequence;
  }

  public Long getChannelId() {
    return channelId;
  }

  public Long getMessageSequence() {
    return messageSequence;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    ChannelSequenceId that = (ChannelSequenceId) o;
    return Objects.equals(channelId, that.channelId)
        && Objects.equals(messageSequence, that.messageSequence);
  }

  @Override
  public int hashCode() {
    return Objects.hash(channelId, messageSequence);
  }

  @Override
  public String toString() {
    return "ChannelSequenceId{channelId=%d, messageSequence=%d}"
        .formatted(channelId, messageSequence);
  }
}
