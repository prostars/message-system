package net.prostars.messagesystem.dto.kafka;

import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.dto.domain.ChannelId;
import net.prostars.messagesystem.dto.domain.MessageSeqId;
import net.prostars.messagesystem.dto.domain.UserId;

public record FetchMessagesRequestRecord(
    UserId userId,
    ChannelId channelId,
    MessageSeqId startMessageSeqId,
    MessageSeqId endMessageSeqId)
    implements RecordInterface {

  @Override
  public String type() {
    return MessageType.FETCH_MESSAGES_REQUEST;
  }
}
