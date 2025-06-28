package net.prostars.messagesystem.handler.kafka;

import net.prostars.messagesystem.dto.kafka.DisconnectResponseRecord;
import net.prostars.messagesystem.dto.websocket.outbound.DisconnectResponse;
import net.prostars.messagesystem.service.ClientNotificationService;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("unused")
public class DisconnectResponseRecordHandler
    implements BaseRecordHandler<DisconnectResponseRecord> {

  private final ClientNotificationService clientNotificationService;

  public DisconnectResponseRecordHandler(ClientNotificationService clientNotificationService) {
    this.clientNotificationService = clientNotificationService;
  }

  @Override
  public void handleRecord(DisconnectResponseRecord disconnectResponseRecord) {
    clientNotificationService.sendMessage(
        disconnectResponseRecord.userId(),
        new DisconnectResponse(
            disconnectResponseRecord.username(), disconnectResponseRecord.status()),
        disconnectResponseRecord);
  }
}
