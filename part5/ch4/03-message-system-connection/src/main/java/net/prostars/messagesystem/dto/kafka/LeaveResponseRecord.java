package net.prostars.messagesystem.dto.kafka;

import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.dto.domain.UserId;

public record LeaveResponseRecord(UserId userId) implements RecordInterface {

  @Override
  public String type() {
    return MessageType.LEAVE_RESPONSE;
  }
}
