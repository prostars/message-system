package net.prostars.messagesystem.dto.kafka;

import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.dto.domain.UserId;

public record InviteNotificationRecord(UserId userId, String username) implements RecordInterface {

  @Override
  public String type() {
    return MessageType.ASK_INVITE;
  }
}
