package net.prostars.messagesystem.dto.websocket.inbound;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.prostars.messagesystem.constant.MessageType;

public class DisconnectRequest extends BaseRequest {

  private final String username;

  @JsonCreator
  public DisconnectRequest(@JsonProperty("username") String username) {
    super(MessageType.DISCONNECT_REQUEST);
    this.username = username;
  }

  public String getUsername() {
    return username;
  }
}
