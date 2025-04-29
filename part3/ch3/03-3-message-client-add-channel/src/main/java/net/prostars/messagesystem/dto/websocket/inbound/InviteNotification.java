package net.prostars.messagesystem.dto.websocket.inbound;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.prostars.messagesystem.constant.MessageType;

public class InviteNotification extends BaseMessage {

  private final String username;

  @JsonCreator
  public InviteNotification(@JsonProperty("username") String username) {
    super(MessageType.ASK_INVITE);
    this.username = username;
  }

  public String getUsername() {
    return username;
  }
}
