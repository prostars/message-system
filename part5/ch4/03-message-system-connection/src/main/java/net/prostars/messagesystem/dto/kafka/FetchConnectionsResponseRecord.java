package net.prostars.messagesystem.dto.kafka;

import java.util.List;
import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.dto.domain.Connection;
import net.prostars.messagesystem.dto.domain.UserId;

public record FetchConnectionsResponseRecord(UserId userId, List<Connection> connections)
    implements RecordInterface {

  @Override
  public String type() {
    return MessageType.FETCH_CONNECTIONS_RESPONSE;
  }
}
