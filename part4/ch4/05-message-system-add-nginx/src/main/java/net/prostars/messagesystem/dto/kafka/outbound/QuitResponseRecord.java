package net.prostars.messagesystem.dto.kafka.outbound;

import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.dto.domain.ChannelId;
import net.prostars.messagesystem.dto.domain.UserId;

public record QuitResponseRecord(UserId userId, ChannelId channelId) implements RecordInterface {

  @Override
  public String type() {
    return MessageType.QUIT_RESPONSE;
  }
}
