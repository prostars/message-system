package net.prostars.messagesystem.dto.websocket.inbound;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.dto.domain.InviteCode;

public class JoinRequest extends BaseRequest {

  private final InviteCode inviteCode;

  @JsonCreator
  public JoinRequest(@JsonProperty("inviteCode") InviteCode inviteCode) {
    super(MessageType.JOIN_REQUEST);
    this.inviteCode = inviteCode;
  }

  public InviteCode getInviteCode() {
    return inviteCode;
  }
}
