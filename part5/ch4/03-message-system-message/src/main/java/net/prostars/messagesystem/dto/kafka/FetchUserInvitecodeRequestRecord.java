package net.prostars.messagesystem.dto.kafka;

import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.dto.domain.UserId;

public record FetchUserInvitecodeRequestRecord(UserId userId) implements RecordInterface {

  @Override
  public String type() {
    return MessageType.FETCH_USER_INVITECODE_REQUEST;
  }
}
