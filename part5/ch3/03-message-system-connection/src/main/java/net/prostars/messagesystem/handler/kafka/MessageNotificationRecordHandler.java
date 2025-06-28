package net.prostars.messagesystem.handler.kafka;

import net.prostars.messagesystem.dto.kafka.MessageNotificationRecord;
import net.prostars.messagesystem.service.MessageService;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("unused")
public class MessageNotificationRecordHandler
    implements BaseRecordHandler<MessageNotificationRecord> {

  private final MessageService messageService;

  public MessageNotificationRecordHandler(MessageService messageService) {
    this.messageService = messageService;
  }

  @Override
  public void handleRecord(MessageNotificationRecord messageNotificationRecord) {
    messageService.sendMessage(messageNotificationRecord);
  }
}
