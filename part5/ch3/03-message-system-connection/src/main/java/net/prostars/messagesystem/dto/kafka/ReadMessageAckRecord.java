package net.prostars.messagesystem.dto.kafka;

import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.dto.domain.ChannelId;
import net.prostars.messagesystem.dto.domain.MessageSeqId;
import net.prostars.messagesystem.dto.domain.UserId;

public record ReadMessageAckRecord(UserId userId, ChannelId channelId, MessageSeqId messageSeqId)
    implements RecordInterface {

  @Override
  public String type() {
    return MessageType.READ_MESSAGE_ACK;
  }
}
