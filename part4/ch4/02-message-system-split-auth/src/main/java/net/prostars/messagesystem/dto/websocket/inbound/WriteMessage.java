package net.prostars.messagesystem.dto.websocket.inbound;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.dto.domain.ChannelId;

public class WriteMessage extends BaseRequest {

  private final ChannelId channelId;
  private final String content;

  @JsonCreator
  public WriteMessage(
      @JsonProperty("channelId") ChannelId channelId, @JsonProperty("content") String content) {
    super(MessageType.WRITE_MESSAGE);
    this.channelId = channelId;
    this.content = content;
  }

  public ChannelId getChannelId() {
    return channelId;
  }

  public String getContent() {
    return content;
  }
}
