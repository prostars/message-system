package net.prostars.messagesystem.dto.kafka.inbound;

import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.dto.domain.ChannelId;
import net.prostars.messagesystem.dto.domain.UserId;

public record CreateResponseRecord(UserId userId, ChannelId channelId, String title)
    implements RecordInterface {
  @Override
  public String type() {
    return MessageType.CREATE_RESPONSE;
  }
}
