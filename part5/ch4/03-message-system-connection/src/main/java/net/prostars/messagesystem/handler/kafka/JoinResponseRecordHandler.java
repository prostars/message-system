package net.prostars.messagesystem.handler.kafka;

import net.prostars.messagesystem.dto.kafka.JoinResponseRecord;
import net.prostars.messagesystem.dto.websocket.outbound.JoinNotification;
import net.prostars.messagesystem.service.ClientNotificationService;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("unused")
public class JoinResponseRecordHandler implements BaseRecordHandler<JoinResponseRecord> {

  private final ClientNotificationService clientNotificationService;

  public JoinResponseRecordHandler(ClientNotificationService clientNotificationService) {
    this.clientNotificationService = clientNotificationService;
  }

  @Override
  public void handleRecord(JoinResponseRecord joinResponseRecord) {
    clientNotificationService.sendMessage(
        joinResponseRecord.userId(),
        new JoinNotification(joinResponseRecord.channelId(), joinResponseRecord.title()),
        joinResponseRecord);
  }
}
