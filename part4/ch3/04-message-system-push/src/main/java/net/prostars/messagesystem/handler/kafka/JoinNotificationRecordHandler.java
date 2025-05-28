package net.prostars.messagesystem.handler.kafka;

import net.prostars.messagesystem.dto.kafka.inbound.JoinNotificationRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class JoinNotificationRecordHandler implements BaseRecordHandler<JoinNotificationRecord> {

  private static final Logger log = LoggerFactory.getLogger(JoinNotificationRecordHandler.class);

  @Override
  public void handleRecord(JoinNotificationRecord record) {
    log.info("{} to offline userId: {}", record, record.userId());
  }
}
