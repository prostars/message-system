package net.prostars.messagesystem.handler.kafka;

import net.prostars.messagesystem.dto.kafka.InviteNotificationRecord;
import net.prostars.messagesystem.dto.websocket.outbound.InviteNotification;
import net.prostars.messagesystem.service.ClientNotificationService;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("unused")
public class InviteNotificationRecordHandler
    implements BaseRecordHandler<InviteNotificationRecord> {

  private final ClientNotificationService clientNotificationService;

  public InviteNotificationRecordHandler(ClientNotificationService clientNotificationService) {
    this.clientNotificationService = clientNotificationService;
  }

  @Override
  public void handleRecord(InviteNotificationRecord inviteNotificationRecord) {
    clientNotificationService.sendMessage(
        inviteNotificationRecord.userId(),
        new InviteNotification(inviteNotificationRecord.username()),
        inviteNotificationRecord);
  }
}
