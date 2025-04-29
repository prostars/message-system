package net.prostars.messagesystem.dto.websocket.outbound;

import net.prostars.messagesystem.constant.MessageType;

public class WriteMessageRequest extends BaseRequest {

  private final String username;
  private final String content;

  public WriteMessageRequest(String username, String content) {
    super(MessageType.WRITE_MESSAGE);
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
