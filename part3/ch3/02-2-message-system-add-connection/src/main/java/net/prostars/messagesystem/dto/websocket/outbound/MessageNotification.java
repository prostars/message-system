package net.prostars.messagesystem.dto.websocket.outbound;

import net.prostars.messagesystem.constant.MessageType;

public class MessageNotification extends BaseMessage {

  private final String username;
  private final String content;

  public MessageNotification(String username, String content) {
    super(MessageType.NOTIFY_MESSAGE);
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
