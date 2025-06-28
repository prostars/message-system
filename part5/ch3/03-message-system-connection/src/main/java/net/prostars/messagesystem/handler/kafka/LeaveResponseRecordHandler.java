package net.prostars.messagesystem.handler.kafka;

import net.prostars.messagesystem.dto.kafka.LeaveResponseRecord;
import net.prostars.messagesystem.dto.websocket.outbound.LeaveResponse;
import net.prostars.messagesystem.service.ClientNotificationService;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("unused")
public class LeaveResponseRecordHandler implements BaseRecordHandler<LeaveResponseRecord> {

  private final ClientNotificationService clientNotificationService;

  public LeaveResponseRecordHandler(ClientNotificationService clientNotificationService) {
    this.clientNotificationService = clientNotificationService;
  }

  @Override
  public void handleRecord(LeaveResponseRecord leaveResponseRecord) {
    clientNotificationService.sendMessage(
        leaveResponseRecord.userId(), new LeaveResponse(), leaveResponseRecord);
  }
}
