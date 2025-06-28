package net.prostars.messagesystem.handler.kafka;

import net.prostars.messagesystem.dto.kafka.QuitResponseRecord;
import net.prostars.messagesystem.dto.websocket.outbound.QuitResponse;
import net.prostars.messagesystem.service.ClientNotificationService;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("unused")
public class QuitResponseRecordHandler implements BaseRecordHandler<QuitResponseRecord> {

  private final ClientNotificationService clientNotificationService;

  public QuitResponseRecordHandler(ClientNotificationService clientNotificationService) {
    this.clientNotificationService = clientNotificationService;
  }

  @Override
  public void handleRecord(QuitResponseRecord quitResponseRecord) {
    clientNotificationService.sendMessage(
        quitResponseRecord.userId(),
        new QuitResponse(quitResponseRecord.channelId()),
        quitResponseRecord);
  }
}
