package net.prostars.messagesystem.handler.kafka;

import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.dto.domain.UserId;
import net.prostars.messagesystem.dto.kafka.ErrorResponseRecord;
import net.prostars.messagesystem.dto.kafka.FetchChannelInviteCodeRequestRecord;
import net.prostars.messagesystem.dto.kafka.FetchChannelInviteCodeResponseRecord;
import net.prostars.messagesystem.service.ChannelService;
import net.prostars.messagesystem.service.ClientNotificationService;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("unused")
public class FetchChannelInviteCodeRequestRecordHandler
    implements BaseRecordHandler<FetchChannelInviteCodeRequestRecord> {

  private final ChannelService channelService;
  private final ClientNotificationService clientNotificationService;

  public FetchChannelInviteCodeRequestRecordHandler(
      ChannelService channelService, ClientNotificationService clientNotificationService) {
    this.channelService = channelService;
    this.clientNotificationService = clientNotificationService;
  }

  @Override
  public void handleRecord(FetchChannelInviteCodeRequestRecord record) {
    UserId senderUserId = record.userId();

    if (!channelService.isJoined(record.channelId(), senderUserId)) {
      clientNotificationService.sendError(
          new ErrorResponseRecord(
              senderUserId,
              MessageType.FETCH_CHANNEL_INVITECODE_REQUEST,
              "Not joined the channel."));
      return;
    }

    channelService
        .getInviteCode(record.channelId())
        .ifPresentOrElse(
            inviteCode ->
                clientNotificationService.sendMessage(
                    senderUserId,
                    new FetchChannelInviteCodeResponseRecord(
                        senderUserId, record.channelId(), inviteCode)),
            () ->
                clientNotificationService.sendError(
                    new ErrorResponseRecord(
                        senderUserId,
                        MessageType.FETCH_CHANNEL_INVITECODE_REQUEST,
                        "Fetch channel invite code failed.")));
  }
}
