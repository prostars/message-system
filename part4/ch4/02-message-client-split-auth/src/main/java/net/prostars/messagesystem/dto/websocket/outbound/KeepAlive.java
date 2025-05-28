package net.prostars.messagesystem.dto.websocket.outbound;

import net.prostars.messagesystem.constant.MessageType;

public class KeepAlive extends BaseRequest {

  public KeepAlive() {
    super(MessageType.KEEP_ALIVE);
  }
}
