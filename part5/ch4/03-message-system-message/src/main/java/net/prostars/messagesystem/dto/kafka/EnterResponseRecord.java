package net.prostars.messagesystem.dto.kafka;

import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.dto.domain.ChannelId;
import net.prostars.messagesystem.dto.domain.MessageSeqId;
import net.prostars.messagesystem.dto.domain.UserId;

public record EnterResponseRecord(
    UserId userId,
    ChannelId channelId,
    String title,
    MessageSeqId lastReadMessageSeqId,
    MessageSeqId lastChannelMessageSeqId)
    implements RecordInterface {

  @Override
  public String type() {
    return MessageType.ENTER_RESPONSE;
  }
}
