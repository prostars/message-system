package net.prostars.messagesystem.dto.websocket.inbound;

import com.fasterxml.jackson.annotation.JsonCreator;
import net.prostars.messagesystem.constant.MessageType;

public class LeaveResponse extends BaseMessage {

  @JsonCreator
  public LeaveResponse() {
    super(MessageType.LEAVE_RESPONSE);
  }
}
