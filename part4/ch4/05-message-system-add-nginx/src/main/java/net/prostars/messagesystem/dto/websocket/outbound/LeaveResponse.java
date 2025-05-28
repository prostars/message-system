package net.prostars.messagesystem.dto.websocket.outbound;

import net.prostars.messagesystem.constant.MessageType;

public class LeaveResponse extends BaseMessage {

  public LeaveResponse() {
    super(MessageType.LEAVE_RESPONSE);
  }
}
