package net.prostars.messagesystem.dto.websocket.outbound;

import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.dto.domain.ChannelId;

public class QuitRequest extends BaseRequest {

  private final ChannelId channelId;

  public QuitRequest(ChannelId channelId) {
    super(MessageType.QUIT_REQUEST);
    this.channelId = channelId;
  }

  public ChannelId getChannelId() {
    return channelId;
  }
}
