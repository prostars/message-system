package net.prostars.messagesystem.dto.domain;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Objects;

public record Message(
    @JsonValue ChannelId channelId,
    @JsonValue MessageSeqId messageSeqId,
    @JsonValue String username,
    @JsonValue String content)
    implements Comparable<Message> {

  @Override
  public int compareTo(Message o) {
    return Long.compare(messageSeqId.id(), o.messageSeqId().id());
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    Message message = (Message) o;
    return Objects.equals(channelId, message.channelId) && Objects.equals(messageSeqId, message.messageSeqId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(channelId, messageSeqId);
  }
}
