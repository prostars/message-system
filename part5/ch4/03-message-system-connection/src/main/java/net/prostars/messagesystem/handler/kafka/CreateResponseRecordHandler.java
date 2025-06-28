package net.prostars.messagesystem.handler.kafka;

import net.prostars.messagesystem.dto.kafka.CreateResponseRecord;
import net.prostars.messagesystem.dto.websocket.outbound.CreateResponse;
import net.prostars.messagesystem.service.ClientNotificationService;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("unused")
public class CreateResponseRecordHandler implements BaseRecordHandler<CreateResponseRecord> {

  private final ClientNotificationService clientNotificationService;

  public CreateResponseRecordHandler(ClientNotificationService clientNotificationService) {
    this.clientNotificationService = clientNotificationService;
  }

  @Override
  public void handleRecord(CreateResponseRecord createResponseRecord) {
    clientNotificationService.sendMessage(
        createResponseRecord.userId(),
        new CreateResponse(createResponseRecord.channelId(), createResponseRecord.title()),
        createResponseRecord);
  }
}
