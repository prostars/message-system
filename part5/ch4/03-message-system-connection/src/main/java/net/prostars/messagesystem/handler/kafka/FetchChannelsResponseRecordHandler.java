package net.prostars.messagesystem.handler.kafka;

import net.prostars.messagesystem.dto.kafka.FetchChannelsResponseRecord;
import net.prostars.messagesystem.dto.websocket.outbound.FetchChannelsResponse;
import net.prostars.messagesystem.service.ClientNotificationService;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("unused")
public class FetchChannelsResponseRecordHandler
    implements BaseRecordHandler<FetchChannelsResponseRecord> {

  private final ClientNotificationService clientNotificationService;

  public FetchChannelsResponseRecordHandler(ClientNotificationService clientNotificationService) {
    this.clientNotificationService = clientNotificationService;
  }

  @Override
  public void handleRecord(FetchChannelsResponseRecord fetchChannelsResponseRecord) {
    clientNotificationService.sendMessage(
        fetchChannelsResponseRecord.userId(),
        new FetchChannelsResponse(fetchChannelsResponseRecord.channels()),
        fetchChannelsResponseRecord);
  }
}
