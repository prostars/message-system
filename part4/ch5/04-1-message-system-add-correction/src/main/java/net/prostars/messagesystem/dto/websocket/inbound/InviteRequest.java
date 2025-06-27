package net.prostars.messagesystem.dto.websocket.inbound;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.dto.domain.InviteCode;

public class InviteRequest extends BaseRequest {

  private final InviteCode userInviteCode;

  @JsonCreator
  public InviteRequest(@JsonProperty("userInviteCode") InviteCode userInviteCode) {
    super(MessageType.INVITE_REQUEST);
    this.userInviteCode = userInviteCode;
  }

  public InviteCode getUserInviteCode() {
    return userInviteCode;
  }
}
