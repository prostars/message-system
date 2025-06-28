package net.prostars.messagesystem.handler.kafka;

import net.prostars.messagesystem.dto.kafka.FetchUserInvitecodeResponseRecord;
import net.prostars.messagesystem.dto.websocket.outbound.FetchUserInvitecodeResponse;
import net.prostars.messagesystem.service.ClientNotificationService;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("unused")
public class FetchUserInvitecodeResponseRecordHandler
    implements BaseRecordHandler<FetchUserInvitecodeResponseRecord> {

  private final ClientNotificationService clientNotificationService;

  public FetchUserInvitecodeResponseRecordHandler(
      ClientNotificationService clientNotificationService) {
    this.clientNotificationService = clientNotificationService;
  }

  @Override
  public void handleRecord(FetchUserInvitecodeResponseRecord fetchUserInvitecodeResponseRecord) {
    clientNotificationService.sendMessage(
        fetchUserInvitecodeResponseRecord.userId(),
        new FetchUserInvitecodeResponse(fetchUserInvitecodeResponseRecord.inviteCode()),
        fetchUserInvitecodeResponseRecord);
  }
}
