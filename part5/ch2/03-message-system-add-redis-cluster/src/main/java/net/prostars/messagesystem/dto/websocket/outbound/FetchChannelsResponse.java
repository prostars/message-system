package net.prostars.messagesystem.dto.websocket.outbound;

import java.util.List;
import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.dto.domain.Channel;

public class FetchChannelsResponse extends BaseMessage {

  private final List<Channel> channels;

  public FetchChannelsResponse(List<Channel> channels) {
    super(MessageType.FETCH_CHANNELS_RESPONSE);
    this.channels = channels;
  }

  public List<Channel> getChannels() {
    return channels;
  }
}
