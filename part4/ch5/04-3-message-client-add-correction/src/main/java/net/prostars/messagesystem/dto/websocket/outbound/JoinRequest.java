package net.prostars.messagesystem.dto.websocket.outbound;

import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.dto.domain.InviteCode;

public class JoinRequest extends BaseRequest {

  private final InviteCode inviteCode;

  public JoinRequest(InviteCode inviteCode) {
    super(MessageType.JOIN_REQUEST);
    this.inviteCode = inviteCode;
  }

  public InviteCode getInviteCode() {
    return inviteCode;
  }
}
