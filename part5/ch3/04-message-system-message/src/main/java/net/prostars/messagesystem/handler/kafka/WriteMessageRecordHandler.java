package net.prostars.messagesystem.handler.kafka;

import net.prostars.messagesystem.dto.kafka.WriteMessageRecord;
import net.prostars.messagesystem.service.MessageService;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("unused")
public class WriteMessageRecordHandler implements BaseRecordHandler<WriteMessageRecord> {

  private final MessageService messageService;
  
  public WriteMessageRecordHandler(MessageService messageService) {
    this.messageService = messageService;
  }

  @Override
  public void handleRecord(WriteMessageRecord record) {
    messageService.sendMessage(record);
  }
}
