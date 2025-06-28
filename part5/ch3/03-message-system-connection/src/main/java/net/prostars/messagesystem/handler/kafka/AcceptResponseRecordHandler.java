package net.prostars.messagesystem.handler.kafka;

import net.prostars.messagesystem.dto.kafka.AcceptResponseRecord;
import net.prostars.messagesystem.dto.websocket.outbound.AcceptResponse;
import net.prostars.messagesystem.service.ClientNotificationService;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("unused")
public class AcceptResponseRecordHandler implements BaseRecordHandler<AcceptResponseRecord> {

  private final ClientNotificationService clientNotificationService;

  public AcceptResponseRecordHandler(ClientNotificationService clientNotificationService) {
    this.clientNotificationService = clientNotificationService;
  }

  @Override
  public void handleRecord(AcceptResponseRecord acceptResponseRecord) {
    clientNotificationService.sendMessage(
        acceptResponseRecord.userId(),
        new AcceptResponse(acceptResponseRecord.username()),
        acceptResponseRecord);
  }
}
