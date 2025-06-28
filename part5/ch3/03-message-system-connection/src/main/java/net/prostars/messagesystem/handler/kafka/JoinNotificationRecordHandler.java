package net.prostars.messagesystem.handler.kafka;

import net.prostars.messagesystem.dto.kafka.JoinNotificationRecord;
import net.prostars.messagesystem.dto.websocket.outbound.JoinNotification;
import net.prostars.messagesystem.service.ClientNotificationService;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("unused")
public class JoinNotificationRecordHandler implements BaseRecordHandler<JoinNotificationRecord> {

  private final ClientNotificationService clientNotificationService;

  public JoinNotificationRecordHandler(ClientNotificationService clientNotificationService) {
    this.clientNotificationService = clientNotificationService;
  }

  @Override
  public void handleRecord(JoinNotificationRecord joinNotificationRecord) {
    clientNotificationService.sendMessage(
        joinNotificationRecord.userId(),
        new JoinNotification(joinNotificationRecord.channelId(), joinNotificationRecord.title()),
        joinNotificationRecord);
  }
}
