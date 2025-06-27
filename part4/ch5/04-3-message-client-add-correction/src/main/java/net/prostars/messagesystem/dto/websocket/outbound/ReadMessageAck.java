package net.prostars.messagesystem.dto.websocket.outbound;

import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.dto.domain.ChannelId;
import net.prostars.messagesystem.dto.domain.MessageSeqId;

import java.util.Objects;

public class ReadMessageAck extends BaseRequest {

  private final ChannelId channelId;
  private final MessageSeqId messageSeqId;

  public ReadMessageAck(ChannelId channelId, MessageSeqId messageSeqId) {
    super(MessageType.READ_MESSAGE_ACK);
    this.channelId = channelId;
    this.messageSeqId = messageSeqId;
  }

  public ChannelId getChannelId() {
    return channelId;
  }

  public MessageSeqId getMessageSeqId() {
    return messageSeqId;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    ReadMessageAck that = (ReadMessageAck) o;
    return Objects.equals(channelId, that.channelId) && Objects.equals(messageSeqId, that.messageSeqId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(channelId, messageSeqId);
  }
}
