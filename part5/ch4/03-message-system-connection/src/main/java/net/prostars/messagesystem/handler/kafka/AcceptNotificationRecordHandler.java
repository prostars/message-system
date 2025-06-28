package net.prostars.messagesystem.handler.kafka;

import net.prostars.messagesystem.dto.kafka.AcceptNotificationRecord;
import net.prostars.messagesystem.dto.websocket.outbound.AcceptNotification;
import net.prostars.messagesystem.service.ClientNotificationService;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("unused")
public class AcceptNotificationRecordHandler
    implements BaseRecordHandler<AcceptNotificationRecord> {

  private final ClientNotificationService clientNotificationService;

  public AcceptNotificationRecordHandler(ClientNotificationService clientNotificationService) {
    this.clientNotificationService = clientNotificationService;
  }

  @Override
  public void handleRecord(AcceptNotificationRecord acceptNotificationRecord) {
    clientNotificationService.sendMessage(
        acceptNotificationRecord.userId(),
        new AcceptNotification(acceptNotificationRecord.username()),
        acceptNotificationRecord);
  }
}
