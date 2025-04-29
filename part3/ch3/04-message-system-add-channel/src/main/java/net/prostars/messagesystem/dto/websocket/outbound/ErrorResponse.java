package net.prostars.messagesystem.dto.websocket.outbound;

import net.prostars.messagesystem.constant.MessageType;

public class ErrorResponse extends BaseMessage {

  private final String messageType;
  private final String message;

  public ErrorResponse(String messageType, String message) {
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
