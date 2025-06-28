package net.prostars.messagesystem.dto.kafka;

import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.constant.UserConnectionStatus;
import net.prostars.messagesystem.dto.domain.UserId;

public record FetchConnectionsRequestRecord(UserId userId, UserConnectionStatus status)
    implements RecordInterface {

  @Override
  public String type() {
    return MessageType.FETCH_CONNECTIONS_REQUEST;
  }
}
