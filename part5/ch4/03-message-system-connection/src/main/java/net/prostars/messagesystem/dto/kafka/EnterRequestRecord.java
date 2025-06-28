package net.prostars.messagesystem.dto.kafka;

import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.dto.domain.ChannelId;
import net.prostars.messagesystem.dto.domain.UserId;

public record EnterRequestRecord(UserId userId, ChannelId channelId) implements RecordInterface {

  @Override
  public String type() {
    return MessageType.ENTER_REQUEST;
  }
}
