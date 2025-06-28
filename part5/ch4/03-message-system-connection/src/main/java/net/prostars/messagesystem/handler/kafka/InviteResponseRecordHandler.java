package net.prostars.messagesystem.handler.kafka;

import net.prostars.messagesystem.dto.kafka.InviteResponseRecord;
import net.prostars.messagesystem.dto.websocket.outbound.InviteResponse;
import net.prostars.messagesystem.service.ClientNotificationService;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("unused")
public class InviteResponseRecordHandler implements BaseRecordHandler<InviteResponseRecord> {

  private final ClientNotificationService clientNotificationService;

  public InviteResponseRecordHandler(ClientNotificationService clientNotificationService) {
    this.clientNotificationService = clientNotificationService;
  }

  @Override
  public void handleRecord(InviteResponseRecord inviteResponseRecord) {
    clientNotificationService.sendMessage(
        inviteResponseRecord.userId(),
        new InviteResponse(inviteResponseRecord.inviteCode(), inviteResponseRecord.status()),
        inviteResponseRecord);
  }
}
