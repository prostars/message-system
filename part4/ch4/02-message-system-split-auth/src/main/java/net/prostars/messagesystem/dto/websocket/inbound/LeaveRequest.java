package net.prostars.messagesystem.dto.websocket.inbound;

import com.fasterxml.jackson.annotation.JsonCreator;
import net.prostars.messagesystem.constant.MessageType;

public class LeaveRequest extends BaseRequest {

  @JsonCreator
  public LeaveRequest() {
    super(MessageType.LEAVE_REQUEST);
  }
}
