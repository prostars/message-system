package net.prostars.messagesystem.handler.kafka;

import net.prostars.messagesystem.dto.kafka.ReadMessageAckRecord;
import net.prostars.messagesystem.service.MessageService;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("unused")
public class ReadMessageAckRecordHandler implements BaseRecordHandler<ReadMessageAckRecord> {

  private final MessageService messageService;

  public ReadMessageAckRecordHandler(MessageService messageService) {
    this.messageService = messageService;
  }

  @Override
  public void handleRecord(ReadMessageAckRecord record) {
    messageService.updateLastReadMsgSeq(record.userId(), record.channelId(), record.messageSeqId());
  }
}
