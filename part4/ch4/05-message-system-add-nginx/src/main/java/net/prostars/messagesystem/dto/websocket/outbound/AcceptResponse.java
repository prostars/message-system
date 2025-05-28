package net.prostars.messagesystem.dto.websocket.outbound;

import net.prostars.messagesystem.constant.MessageType;

public class AcceptResponse extends BaseMessage {

  private final String username;

  public AcceptResponse(String username) {
    super(MessageType.ACCEPT_RESPONSE);
    this.username = username;
  }

  public String getUsername() {
    return username;
  }
}
