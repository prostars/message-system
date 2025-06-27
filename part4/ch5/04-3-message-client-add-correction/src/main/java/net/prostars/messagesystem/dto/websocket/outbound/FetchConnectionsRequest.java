package net.prostars.messagesystem.dto.websocket.outbound;

import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.constant.UserConnectionStatus;

public class FetchConnectionsRequest extends BaseRequest {

  private final UserConnectionStatus status;

  public FetchConnectionsRequest(UserConnectionStatus status) {
    super(MessageType.FETCH_CONNECTIONS_REQUEST);
    this.status = status;
  }

  public UserConnectionStatus getStatus() {
    return status;
  }
}
