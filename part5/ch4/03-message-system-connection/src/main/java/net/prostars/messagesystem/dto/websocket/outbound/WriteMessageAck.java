package net.prostars.messagesystem.dto.websocket.outbound;

import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.dto.domain.MessageSeqId;

public class WriteMessageAck extends BaseMessage {

  private final Long serial;
  private final MessageSeqId messageSeqId;

  public WriteMessageAck(Long serial, MessageSeqId messageSeqId) {
    super(MessageType.WRITE_MESSAGE_ACK);
    this.serial = serial;
    this.messageSeqId = messageSeqId;
  }

  public Long getSerial() {
    return serial;
  }

  public MessageSeqId getMessageSeqId() {
    return messageSeqId;
  }
}
