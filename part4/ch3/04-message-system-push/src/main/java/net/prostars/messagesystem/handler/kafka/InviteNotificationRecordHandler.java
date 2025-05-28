package net.prostars.messagesystem.handler.kafka;

import net.prostars.messagesystem.dto.kafka.inbound.InviteNotificationRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class InviteNotificationRecordHandler
    implements BaseRecordHandler<InviteNotificationRecord> {

  private static final Logger log = LoggerFactory.getLogger(InviteNotificationRecordHandler.class);

  @Override
  public void handleRecord(InviteNotificationRecord record) {
    log.info("{} to offline userId: {}", record, record.userId());
  }
}
