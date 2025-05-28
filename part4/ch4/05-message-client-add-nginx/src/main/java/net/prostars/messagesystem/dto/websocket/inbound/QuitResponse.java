package net.prostars.messagesystem.dto.websocket.inbound;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.dto.domain.ChannelId;

public class QuitResponse extends BaseMessage {

  private final ChannelId channelId;

  @JsonCreator
  public QuitResponse(@JsonProperty("channelId") ChannelId channelId) {
    super(MessageType.QUIT_RESPONSE);
    this.channelId = channelId;
  }

  public ChannelId getChannelId() {
    return channelId;
  }
}
