package net.prostars.messagesystem.dto.websocket.outbound;

import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.dto.domain.ChannelId;

public class QuitResponse extends BaseMessage {

  private final ChannelId channelId;

  public QuitResponse(ChannelId channelId) {
    super(MessageType.QUIT_RESPONSE);
    this.channelId = channelId;
  }

  public ChannelId getChannelId() {
    return channelId;
  }
}
