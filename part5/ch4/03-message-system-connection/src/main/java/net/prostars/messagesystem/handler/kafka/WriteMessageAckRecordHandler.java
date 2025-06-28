package net.prostars.messagesystem.handler.kafka;

import net.prostars.messagesystem.dto.kafka.WriteMessageAckRecord;
import net.prostars.messagesystem.dto.websocket.outbound.WriteMessageAck;
import net.prostars.messagesystem.service.ClientNotificationService;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("unused")
public class WriteMessageAckRecordHandler implements BaseRecordHandler<WriteMessageAckRecord> {

  private final ClientNotificationService clientNotificationService;

  public WriteMessageAckRecordHandler(ClientNotificationService clientNotificationService) {
    this.clientNotificationService = clientNotificationService;
  }

  @Override
  public void handleRecord(WriteMessageAckRecord writeMessageAckRecord) {
    clientNotificationService.sendMessage(
        writeMessageAckRecord.userId(),
        new WriteMessageAck(writeMessageAckRecord.serial(), writeMessageAckRecord.messageSeqId()),
        writeMessageAckRecord);
  }
}
