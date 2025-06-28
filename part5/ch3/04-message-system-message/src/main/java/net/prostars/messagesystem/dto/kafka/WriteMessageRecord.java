package net.prostars.messagesystem.dto.kafka;

import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.dto.domain.ChannelId;
import net.prostars.messagesystem.dto.domain.MessageSeqId;
import net.prostars.messagesystem.dto.domain.UserId;

public record WriteMessageRecord(UserId userId, Long serial, ChannelId channelId, String content, MessageSeqId messageSeqId)
    implements RecordInterface {

  @Override
  public String type() {
    return MessageType.WRITE_MESSAGE;
  }
}
