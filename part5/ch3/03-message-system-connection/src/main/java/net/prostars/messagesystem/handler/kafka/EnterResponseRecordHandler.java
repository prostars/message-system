package net.prostars.messagesystem.handler.kafka;

import net.prostars.messagesystem.dto.kafka.EnterResponseRecord;
import net.prostars.messagesystem.dto.websocket.outbound.EnterResponse;
import net.prostars.messagesystem.service.ClientNotificationService;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("unused")
public class EnterResponseRecordHandler implements BaseRecordHandler<EnterResponseRecord> {

  private final ClientNotificationService clientNotificationService;

  public EnterResponseRecordHandler(ClientNotificationService clientNotificationService) {
    this.clientNotificationService = clientNotificationService;
  }

  @Override
  public void handleRecord(EnterResponseRecord enterResponseRecord) {
    clientNotificationService.sendMessage(
        enterResponseRecord.userId(),
        new EnterResponse(
            enterResponseRecord.channelId(),
            enterResponseRecord.title(),
            enterResponseRecord.lastReadMessageSeqId(),
            enterResponseRecord.lastChannelMessageSeqId()),
        enterResponseRecord);
  }
}
