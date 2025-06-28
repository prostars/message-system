package net.prostars.messagesystem.dto.kafka;

import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.constant.UserConnectionStatus;
import net.prostars.messagesystem.dto.domain.InviteCode;
import net.prostars.messagesystem.dto.domain.UserId;

public record InviteResponseRecord(UserId userId, InviteCode inviteCode, UserConnectionStatus status)
    implements RecordInterface {

  @Override
  public String type() {
    return MessageType.INVITE_RESPONSE;
  }
}
