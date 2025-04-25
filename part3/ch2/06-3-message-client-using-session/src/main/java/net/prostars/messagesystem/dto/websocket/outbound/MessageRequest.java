package net.prostars.messagesystem.dto.websocket.outbound;

import net.prostars.messagesystem.constant.MessageType;

public class MessageRequest extends BaseRequest {

  private final String username;
  private final String content;

  public MessageRequest(String username, String content) {
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
