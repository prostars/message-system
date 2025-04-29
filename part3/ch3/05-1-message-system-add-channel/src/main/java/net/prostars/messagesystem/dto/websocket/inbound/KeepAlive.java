package net.prostars.messagesystem.dto.websocket.inbound;

import com.fasterxml.jackson.annotation.JsonCreator;
import net.prostars.messagesystem.constant.MessageType;

public class KeepAlive extends BaseRequest {

  @JsonCreator
  public KeepAlive() {
    super(MessageType.KEEP_ALIVE);
  }
}
