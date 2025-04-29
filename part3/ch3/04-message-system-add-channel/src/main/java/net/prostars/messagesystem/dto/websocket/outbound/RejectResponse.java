package net.prostars.messagesystem.dto.websocket.outbound;

import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.constant.UserConnectionStatus;

public class RejectResponse extends BaseMessage {

  private final String username;
  private final UserConnectionStatus status;

  public RejectResponse(String username, UserConnectionStatus status) {
    super(MessageType.REJECT_RESPONSE);
    this.username = username;
    this.status = status;
  }

  public String getUsername() {
    return username;
  }

  public UserConnectionStatus getStatus() {
    return status;
  }
}
