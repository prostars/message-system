package net.prostars.messagesystem.dto.websocket.outbound;

import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.constant.UserConnectionStatus;
import net.prostars.messagesystem.dto.domain.InviteCode;

public class InviteResponse extends BaseMessage {

  private final InviteCode inviteCode;
  private final UserConnectionStatus status;

  public InviteResponse(String type, InviteCode inviteCode, UserConnectionStatus status) {
    super(MessageType.INVITE_RESPONSE);
    this.inviteCode = inviteCode;
    this.status = status;
  }

  public InviteCode getInviteCode() {
    return inviteCode;
  }

  public UserConnectionStatus getStatus() {
    return status;
  }
}
