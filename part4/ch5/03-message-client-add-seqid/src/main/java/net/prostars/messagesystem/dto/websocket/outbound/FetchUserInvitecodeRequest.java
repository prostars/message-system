package net.prostars.messagesystem.dto.websocket.outbound;

import net.prostars.messagesystem.constant.MessageType;

public class FetchUserInvitecodeRequest extends BaseRequest {

  public FetchUserInvitecodeRequest() {
    super(MessageType.FETCH_USER_INVITECODE_REQUEST);
  }
}
