package net.prostars.messagesystem.dto.websocket.outbound;

import net.prostars.messagesystem.constant.MessageType;

public class FetchChannelsRequest extends BaseRequest {

  public FetchChannelsRequest() {
    super(MessageType.FETCH_CHANNELS_REQUEST);
  }
}
