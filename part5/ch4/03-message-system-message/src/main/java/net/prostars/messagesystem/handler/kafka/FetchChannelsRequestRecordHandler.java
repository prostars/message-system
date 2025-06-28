package net.prostars.messagesystem.handler.kafka;

import net.prostars.messagesystem.dto.domain.UserId;
import net.prostars.messagesystem.dto.kafka.FetchChannelsRequestRecord;
import net.prostars.messagesystem.dto.kafka.FetchChannelsResponseRecord;
import net.prostars.messagesystem.service.ChannelService;
import net.prostars.messagesystem.service.ClientNotificationService;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("unused")
public class FetchChannelsRequestRecordHandler
    implements BaseRecordHandler<FetchChannelsRequestRecord> {

  private final ChannelService channelService;
  private final ClientNotificationService clientNotificationService;

  public FetchChannelsRequestRecordHandler(
      ChannelService channelService, ClientNotificationService clientNotificationService) {
    this.channelService = channelService;
    this.clientNotificationService = clientNotificationService;
  }

  @Override
  public void handleRecord(FetchChannelsRequestRecord record) {
    UserId senderUserId = record.userId();

    clientNotificationService.sendMessage(
        senderUserId,
        new FetchChannelsResponseRecord(senderUserId, channelService.getChannels(senderUserId)));
  }
}
