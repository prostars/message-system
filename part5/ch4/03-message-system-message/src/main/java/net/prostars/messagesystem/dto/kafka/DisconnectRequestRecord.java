package net.prostars.messagesystem.dto.kafka;

import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.dto.domain.UserId;

public record DisconnectRequestRecord(UserId userId, String username) implements RecordInterface {

  @Override
  public String type() {
    return MessageType.DISCONNECT_REQUEST;
  }
}
