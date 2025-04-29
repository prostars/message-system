package net.prostars.messagesystem.dto.websocket.inbound;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.prostars.messagesystem.constant.MessageType;

public class ErrorResponse extends BaseMessage {

  private final String messageType;
  private final String message;

  @JsonCreator
  public ErrorResponse(
      @JsonProperty("messageType") String messageType, @JsonProperty("message") String message) {
    super(MessageType.ERROR);
    this.messageType = messageType;
    this.message = message;
  }

  public String getMessageType() {
    return messageType;
  }

  public String getMessage() {
    return message;
  }
}
