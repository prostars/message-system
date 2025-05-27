package net.prostars.messagesystem.dto.websocket.outbound;

import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.dto.domain.InviteCode;

public class FetchUserInvitecodeResponse extends BaseMessage {

  private final InviteCode inviteCode;

  public FetchUserInvitecodeResponse(InviteCode inviteCode) {
    super(MessageType.FETCH_USER_INVITECODE_RESPONSE);
    this.inviteCode = inviteCode;
  }

  public InviteCode getInviteCode() {
    return inviteCode;
  }
}
