package net.prostars.messagesystem.dto.websocket.inbound;

import com.fasterxml.jackson.annotation.JsonCreator;
import net.prostars.messagesystem.constant.MessageType;

public class KeepAliveRequest extends BaseRequest {

  @JsonCreator
  public KeepAliveRequest() {
    super(MessageType.KEEP_ALIVE);
  }
}
