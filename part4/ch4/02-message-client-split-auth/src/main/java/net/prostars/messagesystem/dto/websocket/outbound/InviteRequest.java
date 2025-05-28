package net.prostars.messagesystem.dto.websocket.outbound;

import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.dto.domain.InviteCode;

public class InviteRequest extends BaseRequest {

  private final InviteCode userInviteCode;

  public InviteRequest(InviteCode userInviteCode) {
    super(MessageType.INVITE_REQUEST);
    this.userInviteCode = userInviteCode;
  }

  public InviteCode getUserInviteCode() {
    return userInviteCode;
  }
}
