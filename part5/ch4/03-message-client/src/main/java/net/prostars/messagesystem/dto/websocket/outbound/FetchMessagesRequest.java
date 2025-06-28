package net.prostars.messagesystem.dto.websocket.outbound;

import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.dto.domain.ChannelId;
import net.prostars.messagesystem.dto.domain.MessageSeqId;

import java.util.Objects;

public class FetchMessagesRequest extends BaseRequest {

  private final ChannelId channelId;
  private final MessageSeqId startMessageSeqId;
  private final MessageSeqId endMessageSeqId;

  public FetchMessagesRequest(
      ChannelId channelId, MessageSeqId startMessageSeqId, MessageSeqId endMessageSeqId) {
    super(MessageType.FETCH_MESSAGES_REQUEST);
    this.channelId = channelId;
    this.startMessageSeqId = startMessageSeqId;
    this.endMessageSeqId = endMessageSeqId;
  }

  public ChannelId getChannelId() {
    return channelId;
  }

  public MessageSeqId getStartMessageSeqId() {
    return startMessageSeqId;
  }

  public MessageSeqId getEndMessageSeqId() {
    return endMessageSeqId;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    FetchMessagesRequest that = (FetchMessagesRequest) o;
    return Objects.equals(channelId, that.channelId) && Objects.equals(startMessageSeqId, that.startMessageSeqId) && Objects.equals(endMessageSeqId, that.endMessageSeqId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(channelId, startMessageSeqId, endMessageSeqId);
  }
}
