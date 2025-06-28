package net.prostars.messagesystem.dto.kafka;

import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.dto.domain.UserId;

public record ErrorResponseRecord(UserId userId, String messageType, String message)
    implements RecordInterface {

  @Override
  public String type() {
    return MessageType.ERROR;
  }
}
