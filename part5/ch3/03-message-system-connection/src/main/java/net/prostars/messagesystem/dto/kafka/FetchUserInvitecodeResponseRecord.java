package net.prostars.messagesystem.dto.kafka;

import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.dto.domain.InviteCode;
import net.prostars.messagesystem.dto.domain.UserId;

public record FetchUserInvitecodeResponseRecord(UserId userId, InviteCode inviteCode)
    implements RecordInterface {

  @Override
  public String type() {
    return MessageType.FETCH_USER_INVITECODE_RESPONSE;
  }
}
