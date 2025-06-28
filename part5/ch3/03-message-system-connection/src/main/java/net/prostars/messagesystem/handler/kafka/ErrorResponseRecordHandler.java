package net.prostars.messagesystem.handler.kafka;

import net.prostars.messagesystem.dto.kafka.ErrorResponseRecord;
import net.prostars.messagesystem.dto.websocket.outbound.ErrorResponse;
import net.prostars.messagesystem.service.ClientNotificationService;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("unused")
public class ErrorResponseRecordHandler implements BaseRecordHandler<ErrorResponseRecord> {

  private final ClientNotificationService clientNotificationService;

  public ErrorResponseRecordHandler(ClientNotificationService clientNotificationService) {
    this.clientNotificationService = clientNotificationService;
  }

  @Override
  public void handleRecord(ErrorResponseRecord errorResponseRecord) {
    clientNotificationService.sendMessage(
        errorResponseRecord.userId(),
        new ErrorResponse(errorResponseRecord.messageType(), errorResponseRecord.message()),
        errorResponseRecord);
  }
}
