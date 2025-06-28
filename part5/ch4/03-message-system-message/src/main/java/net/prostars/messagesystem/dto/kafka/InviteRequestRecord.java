package net.prostars.messagesystem.dto.kafka;

import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.dto.domain.InviteCode;
import net.prostars.messagesystem.dto.domain.UserId;

public record InviteRequestRecord(UserId userId, InviteCode userInviteCode)
    implements RecordInterface {

  @Override
  public String type() {
    return MessageType.INVITE_REQUEST;
  }
}
