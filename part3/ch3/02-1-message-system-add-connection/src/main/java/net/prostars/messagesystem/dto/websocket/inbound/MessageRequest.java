package net.prostars.messagesystem.dto.websocket.inbound;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.prostars.messagesystem.constant.MessageType;

public class MessageRequest extends BaseRequest {

  private final String username;
  private final String content;

  @JsonCreator
  public MessageRequest(
      @JsonProperty("username") String username, @JsonProperty("content") String content) {
    super(MessageType.MESSAGE);
    this.username = username;
    this.content = content;
  }

  public String getUsername() {
    return username;
  }

  public String getContent() {
    return content;
  }
}
