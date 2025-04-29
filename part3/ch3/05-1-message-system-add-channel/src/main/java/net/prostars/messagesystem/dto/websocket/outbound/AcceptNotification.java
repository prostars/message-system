package net.prostars.messagesystem.dto.websocket.outbound;

import net.prostars.messagesystem.constant.MessageType;

public class AcceptNotification extends BaseMessage {

  private final String username;

  public AcceptNotification(String username) {
    super(MessageType.NOTIFY_ACCEPT);
    this.username = username;
  }

  public String getUsername() {
    return username;
  }
}
