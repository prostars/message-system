package net.prostars.messagesystem.dto.websocket.outbound;

import java.util.List;
import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.dto.domain.Connection;

public class FetchConnectionsResponse extends BaseMessage {

  private final List<Connection> connections;

  public FetchConnectionsResponse(List<Connection> connections) {
    super(MessageType.FETCH_CONNECTIONS_RESPONSE);
    this.connections = connections;
  }

  public List<Connection> getConnections() {
    return connections;
  }
}
