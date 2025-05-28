package net.prostars.messagesystem.dto.kafka.inbound;

import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.dto.domain.ChannelId;
import net.prostars.messagesystem.dto.domain.UserId;

public record MessageNotificationRecord(
    UserId userId, ChannelId channelId, String username, String content)
    implements RecordInterface {

  @Override
  public String type() {
    return MessageType.NOTIFY_MESSAGE;
  }
}
