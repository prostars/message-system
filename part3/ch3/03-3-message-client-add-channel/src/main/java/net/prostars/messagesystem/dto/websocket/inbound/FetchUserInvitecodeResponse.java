package net.prostars.messagesystem.dto.websocket.inbound;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.dto.domain.InviteCode;

public class FetchUserInvitecodeResponse extends BaseMessage {

  private final InviteCode inviteCode;

  @JsonCreator
  public FetchUserInvitecodeResponse(@JsonProperty("inviteCode") InviteCode inviteCode) {
    super(MessageType.FETCH_USER_INVITECODE_RESPONSE);
    this.inviteCode = inviteCode;
  }

  public InviteCode getInviteCode() {
    return inviteCode;
  }
}
