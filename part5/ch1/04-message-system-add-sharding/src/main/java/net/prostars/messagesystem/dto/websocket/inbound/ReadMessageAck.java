package net.prostars.messagesystem.dto.websocket.inbound;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.dto.domain.ChannelId;
import net.prostars.messagesystem.dto.domain.MessageSeqId;

public class ReadMessageAck extends BaseRequest {

  private final ChannelId channelId;
  private final MessageSeqId messageSeqId;

  @JsonCreator
  public ReadMessageAck(
      @JsonProperty("channelId") ChannelId channelId,
      @JsonProperty("messageSeqId") MessageSeqId messageSeqId) {
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
}
