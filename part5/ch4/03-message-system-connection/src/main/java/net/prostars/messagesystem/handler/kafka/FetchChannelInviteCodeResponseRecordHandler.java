package net.prostars.messagesystem.handler.kafka;

import net.prostars.messagesystem.dto.kafka.FetchChannelInviteCodeResponseRecord;
import net.prostars.messagesystem.dto.websocket.outbound.FetchChannelInviteCodeResponse;
import net.prostars.messagesystem.service.ClientNotificationService;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("unused")
public class FetchChannelInviteCodeResponseRecordHandler
    implements BaseRecordHandler<FetchChannelInviteCodeResponseRecord> {

  private final ClientNotificationService clientNotificationService;

  public FetchChannelInviteCodeResponseRecordHandler(
      ClientNotificationService clientNotificationService) {
    this.clientNotificationService = clientNotificationService;
  }

  @Override
  public void handleRecord(
      FetchChannelInviteCodeResponseRecord fetchChannelInviteCodeResponseRecord) {
    clientNotificationService.sendMessage(
        fetchChannelInviteCodeResponseRecord.userId(),
        new FetchChannelInviteCodeResponse(
            fetchChannelInviteCodeResponseRecord.channelId(),
            fetchChannelInviteCodeResponseRecord.inviteCode()),
        fetchChannelInviteCodeResponseRecord);
  }
}
