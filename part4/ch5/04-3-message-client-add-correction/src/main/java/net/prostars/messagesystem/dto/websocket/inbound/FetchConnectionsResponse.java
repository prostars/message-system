package net.prostars.messagesystem.dto.websocket.inbound;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.dto.domain.Connection;

public class FetchConnectionsResponse extends BaseMessage {

  private final List<Connection> connections;

  @JsonCreator
  public FetchConnectionsResponse(@JsonProperty("connections") List<Connection> connections) {
    super(MessageType.FETCH_CONNECTIONS_RESPONSE);
    this.connections = connections;
  }

  public List<Connection> getConnections() {
    return connections;
  }
}
