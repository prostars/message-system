package net.prostars.messagesystem.handler.kafka;

import net.prostars.messagesystem.dto.kafka.RejectResponseRecord;
import net.prostars.messagesystem.dto.websocket.outbound.RejectResponse;
import net.prostars.messagesystem.service.ClientNotificationService;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("unused")
public class RejectResponseRecordHandler implements BaseRecordHandler<RejectResponseRecord> {

  private final ClientNotificationService clientNotificationService;

  public RejectResponseRecordHandler(ClientNotificationService clientNotificationService) {
    this.clientNotificationService = clientNotificationService;
  }

  @Override
  public void handleRecord(RejectResponseRecord rejectResponseRecord) {
    clientNotificationService.sendMessage(
        rejectResponseRecord.userId(),
        new RejectResponse(rejectResponseRecord.username(), rejectResponseRecord.status()),
        rejectResponseRecord);
  }
}
