package net.prostars.messagesystem.dto.kafka;

import java.util.List;
import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.dto.domain.UserId;

public record CreateRequestRecord(UserId userId, String title, List<String> participantUsernames)
    implements RecordInterface {

  @Override
  public String type() {
    return MessageType.CREATE_REQUEST;
  }
}
