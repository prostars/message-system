package net.prostars.messagesystem.dto.kafka;

import java.util.List;
import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.dto.domain.Channel;
import net.prostars.messagesystem.dto.domain.UserId;

public record FetchChannelsResponseRecord(UserId userId, List<Channel> channels)
    implements RecordInterface {

  @Override
  public String type() {
    return MessageType.FETCH_CHANNELS_RESPONSE;
  }
}
