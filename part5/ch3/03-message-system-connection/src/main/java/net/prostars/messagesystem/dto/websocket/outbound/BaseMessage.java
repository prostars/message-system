package net.prostars.messagesystem.dto.websocket.outbound;

public abstract class BaseMessage {

  private final String type;

  public BaseMessage(String type) {
    this.type = type;
  }

  public String getType() {
    return type;
  }
}
