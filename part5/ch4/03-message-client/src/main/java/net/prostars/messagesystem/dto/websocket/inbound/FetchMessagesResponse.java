package net.prostars.messagesystem.dto.websocket.inbound;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.dto.domain.ChannelId;
import net.prostars.messagesystem.dto.domain.Message;

public class FetchMessagesResponse extends BaseMessage {

  private final ChannelId channelId;
  private final List<Message> messages;

  @JsonCreator
  public FetchMessagesResponse(
      @JsonProperty("channelId") ChannelId channelId,
      @JsonProperty("messages") List<Message> messages) {
    super(MessageType.FETCH_MESSAGES_RESPONSE);
    this.channelId = channelId;
    this.messages = messages;
  }

  public ChannelId getChannelId() {
    return channelId;
  }

  public List<Message> getMessages() {
    return messages;
  }
}
