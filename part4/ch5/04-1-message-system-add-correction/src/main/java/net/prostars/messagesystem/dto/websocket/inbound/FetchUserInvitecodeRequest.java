package net.prostars.messagesystem.dto.websocket.inbound;

import com.fasterxml.jackson.annotation.JsonCreator;
import net.prostars.messagesystem.constant.MessageType;

public class FetchUserInvitecodeRequest extends BaseRequest {

  @JsonCreator
  public FetchUserInvitecodeRequest() {
    super(MessageType.FETCH_USER_INVITECODE_REQUEST);
  }
}
