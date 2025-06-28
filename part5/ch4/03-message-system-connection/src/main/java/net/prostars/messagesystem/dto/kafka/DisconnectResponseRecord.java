package net.prostars.messagesystem.dto.kafka;

import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.constant.UserConnectionStatus;
import net.prostars.messagesystem.dto.domain.UserId;

public record DisconnectResponseRecord(UserId userId, String username, UserConnectionStatus status)
    implements RecordInterface {

  @Override
  public String type() {
    return MessageType.DISCONNECT_RESPONSE;
  }
}
