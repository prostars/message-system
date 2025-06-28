package net.prostars.messagesystem.dto.kafka;

import java.util.List;
import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.dto.domain.ChannelId;
import net.prostars.messagesystem.dto.domain.Message;
import net.prostars.messagesystem.dto.domain.UserId;

public record FetchMessagesResponseRecord(
    UserId userId, ChannelId channelId, List<Message> messages) implements RecordInterface {

  @Override
  public String type() {
    return MessageType.FETCH_MESSAGES_RESPONSE;
  }
}
