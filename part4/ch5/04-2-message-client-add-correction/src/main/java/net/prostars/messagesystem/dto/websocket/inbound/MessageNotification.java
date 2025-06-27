package net.prostars.messagesystem.dto.websocket.inbound;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.dto.domain.ChannelId;
import net.prostars.messagesystem.dto.domain.MessageSeqId;

public class MessageNotification extends BaseMessage {

  private final ChannelId channelId;
  private final MessageSeqId messageSeqId;
  private final String username;
  private final String content;

  @JsonCreator
  public MessageNotification(
      @JsonProperty("channelId") ChannelId channelId,
      @JsonProperty("messageSeqId") MessageSeqId messageSeqId,
      @JsonProperty("username") String username,
      @JsonProperty("content") String content) {
    super(MessageType.NOTIFY_MESSAGE);
    this.channelId = channelId;
    this.messageSeqId = messageSeqId;
    this.username = username;
    this.content = content;
  }

  public ChannelId getChannelId() {
    return channelId;
  }

  public MessageSeqId getMessageSeqId() {
    return messageSeqId;
  }

  public String getUsername() {
    return username;
  }

  public String getContent() {
    return content;
  }
}
