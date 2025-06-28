package net.prostars.messagesystem.dto.kafka;

import java.util.List;
import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.dto.domain.ChannelId;
import net.prostars.messagesystem.dto.domain.MessageSeqId;
import net.prostars.messagesystem.dto.domain.UserId;

public record MessageNotificationRecord(
    UserId userId,
    ChannelId channelId,
    MessageSeqId messageSeqId,
    String username,
    String content,
    List<UserId> participantIds)
    implements RecordInterface {

  @Override
  public String type() {
    return MessageType.NOTIFY_MESSAGE;
  }
}
