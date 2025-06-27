package net.prostars.messagesystem.dto.kafka.outbound;

import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.dto.domain.UserId;

public record AcceptNotificationRecord(UserId userId, String username) implements RecordInterface {

  @Override
  public String type() {
    return MessageType.NOTIFY_ACCEPT;
  }
}
