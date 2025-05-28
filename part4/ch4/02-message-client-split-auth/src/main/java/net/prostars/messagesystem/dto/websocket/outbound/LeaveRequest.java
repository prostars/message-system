package net.prostars.messagesystem.dto.websocket.outbound;

import net.prostars.messagesystem.constant.MessageType;

public class LeaveRequest extends BaseRequest {

  public LeaveRequest() {
    super(MessageType.LEAVE_REQUEST);
  }
}
