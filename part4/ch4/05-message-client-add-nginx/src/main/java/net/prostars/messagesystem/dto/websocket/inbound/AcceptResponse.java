package net.prostars.messagesystem.dto.websocket.inbound;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.prostars.messagesystem.constant.MessageType;

public class AcceptResponse extends BaseMessage {

  private final String username;

  @JsonCreator
  public AcceptResponse(@JsonProperty("username") String username) {
    super(MessageType.ACCEPT_RESPONSE);
    this.username = username;
  }

  public String getUsername() {
    return username;
  }
}
