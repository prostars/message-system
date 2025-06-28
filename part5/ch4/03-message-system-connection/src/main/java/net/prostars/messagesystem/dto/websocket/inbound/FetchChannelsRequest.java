package net.prostars.messagesystem.dto.websocket.inbound;

import com.fasterxml.jackson.annotation.JsonCreator;
import net.prostars.messagesystem.constant.MessageType;

public class FetchChannelsRequest extends BaseRequest {

  @JsonCreator
  public FetchChannelsRequest() {
    super(MessageType.FETCH_CHANNELS_REQUEST);
  }
}
