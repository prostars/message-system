package net.prostars.messagesystem.dto.websocket.inbound;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.dto.domain.ChannelId;

public class EnterResponse extends BaseMessage {

  private final ChannelId channelId;
  private final String title;

  @JsonCreator
  public EnterResponse(
      @JsonProperty("channelId") ChannelId channelId, @JsonProperty("title") String title) {
    super(MessageType.ENTER_RESPONSE);
    this.channelId = channelId;
    this.title = title;
  }

  public ChannelId getChannelId() {
    return channelId;
  }

  public String getTitle() {
    return title;
  }
}
