package net.prostars.messagesystem.handler.kafka;

import net.prostars.messagesystem.dto.kafka.FetchConnectionsResponseRecord;
import net.prostars.messagesystem.dto.websocket.outbound.FetchConnectionsResponse;
import net.prostars.messagesystem.service.ClientNotificationService;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("unused")
public class FetchConnectionsResponseRecordHandler
    implements BaseRecordHandler<FetchConnectionsResponseRecord> {

  private final ClientNotificationService clientNotificationService;

  public FetchConnectionsResponseRecordHandler(
      ClientNotificationService clientNotificationService) {
    this.clientNotificationService = clientNotificationService;
  }

  @Override
  public void handleRecord(FetchConnectionsResponseRecord fetchConnectionsResponseRecord) {
    clientNotificationService.sendMessage(
        fetchConnectionsResponseRecord.userId(),
        new FetchConnectionsResponse(fetchConnectionsResponseRecord.connections()),
        fetchConnectionsResponseRecord);
  }
}
