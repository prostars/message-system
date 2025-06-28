package net.prostars.messagesystem.dto.kafka;

import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.dto.domain.MessageSeqId;
import net.prostars.messagesystem.dto.domain.UserId;

public record WriteMessageAckRecord(UserId userId, Long serial, MessageSeqId messageSeqId)
    implements RecordInterface {

  @Override
  public String type() {
    return MessageType.WRITE_MESSAGE_ACK;
  }
}
