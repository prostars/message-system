package net.prostars.messagesystem.handler.kafka;

import net.prostars.messagesystem.dto.kafka.inbound.MessageNotificationRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MessageNotificationRecordHandler
    implements BaseRecordHandler<MessageNotificationRecord> {

  private static final Logger log = LoggerFactory.getLogger(MessageNotificationRecordHandler.class);

  @Override
  public void handleRecord(MessageNotificationRecord record) {
    log.info("{} to offline userId: {}", record, record.userId());
  }
}
