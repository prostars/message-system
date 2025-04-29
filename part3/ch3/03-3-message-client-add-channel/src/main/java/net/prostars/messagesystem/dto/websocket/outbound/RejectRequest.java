package net.prostars.messagesystem.dto.websocket.outbound;

import net.prostars.messagesystem.constant.MessageType;

public class RejectRequest extends BaseRequest {

  private final String username;

  public RejectRequest(String username) {
    super(MessageType.REJECT_REQUEST);
    this.username = username;
  }

  public String getUsername() {
    return username;
  }
}
