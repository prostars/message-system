package net.prostars.messagesystem.dto.kafka;

import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.dto.domain.ChannelId;
import net.prostars.messagesystem.dto.domain.InviteCode;
import net.prostars.messagesystem.dto.domain.UserId;

public record FetchChannelInviteCodeResponseRecord(
    UserId userId, ChannelId channelId, InviteCode inviteCode) implements RecordInterface {

  @Override
  public String type() {
    return MessageType.FETCH_CHANNEL_INVITECODE_RESPONSE;
  }
}
