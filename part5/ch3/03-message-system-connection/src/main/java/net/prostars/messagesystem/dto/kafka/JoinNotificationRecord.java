package net.prostars.messagesystem.dto.kafka;

import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.dto.domain.ChannelId;
import net.prostars.messagesystem.dto.domain.UserId;

public record JoinNotificationRecord(UserId userId, ChannelId channelId, String title)
    implements RecordInterface {

  @Override
  public String type() {
    return MessageType.NOTIFY_JOIN;
  }
}
