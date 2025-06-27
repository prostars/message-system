package net.prostars.messagesystem.dto.websocket.inbound;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.dto.domain.Channel;

public class FetchChannelsResponse extends BaseMessage {

  private final List<Channel> channels;

  @JsonCreator
  public FetchChannelsResponse(@JsonProperty("channels") List<Channel> channels) {
    super(MessageType.FETCH_CHANNELS_RESPONSE);
    this.channels = channels;
  }

  public List<Channel> getChannels() {
    return channels;
  }
}
