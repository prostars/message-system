package net.prostars.messagesystem.dto.websocket.outbound;

import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.dto.domain.ChannelId;
import net.prostars.messagesystem.dto.domain.InviteCode;

public class FetchChannelInviteCodeResponse extends BaseMessage {

  private final ChannelId channelId;
  private final InviteCode inviteCode;

  public FetchChannelInviteCodeResponse(ChannelId channelId, InviteCode inviteCode) {
    super(MessageType.FETCH_CHANNEL_INVITECODE_RESPONSE);
    this.channelId = channelId;
    this.inviteCode = inviteCode;
  }

  public ChannelId getChannelId() {
    return channelId;
  }

  public InviteCode getInviteCode() {
    return inviteCode;
  }
}
