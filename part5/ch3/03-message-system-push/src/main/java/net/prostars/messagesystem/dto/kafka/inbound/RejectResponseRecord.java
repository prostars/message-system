package net.prostars.messagesystem.dto.kafka.inbound;

import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.constant.UserConnectionStatus;
import net.prostars.messagesystem.dto.domain.UserId;

public record RejectResponseRecord(UserId userId, String username, UserConnectionStatus status)
    implements RecordInterface {

  @Override
  public String type() {
    return MessageType.REJECT_RESPONSE;
  }
}
