package net.prostars.messagesystem.dto.websocket.outbound;

import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.dto.domain.ChannelId;

public class EnterRequest extends BaseRequest {

  private final ChannelId channelId;

  public EnterRequest(ChannelId channelId) {
    super(MessageType.ENTER_REQUEST);
    this.channelId = channelId;
  }

  public ChannelId getChannelId() {
    return channelId;
  }
}
