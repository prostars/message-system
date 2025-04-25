package net.prostars.messagesystem.dto.websocket.outbound;

import net.prostars.messagesystem.constant.MessageType;

public class KeepAliveRequest extends BaseRequest {

  public KeepAliveRequest() {
    super(MessageType.KEEP_ALIVE);
  }
}
