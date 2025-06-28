package net.prostars.messagesystem.handler.kafka;

import net.prostars.messagesystem.dto.kafka.FetchMessagesResponseRecord;
import net.prostars.messagesystem.dto.websocket.outbound.FetchMessagesResponse;
import net.prostars.messagesystem.service.ClientNotificationService;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("unused")
public class FetchMessagesResponseRecordHandler
    implements BaseRecordHandler<FetchMessagesResponseRecord> {

  private final ClientNotificationService clientNotificationService;

  public FetchMessagesResponseRecordHandler(ClientNotificationService clientNotificationService) {
    this.clientNotificationService = clientNotificationService;
  }

  @Override
  public void handleRecord(FetchMessagesResponseRecord fetchMessagesResponseRecord) {
    clientNotificationService.sendMessage(
        fetchMessagesResponseRecord.userId(),
        new FetchMessagesResponse(
            fetchMessagesResponseRecord.channelId(), fetchMessagesResponseRecord.messages()),
        fetchMessagesResponseRecord);
  }
}
