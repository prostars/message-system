package net.prostars.messagesystem.handler.kafka;

import net.prostars.messagesystem.dto.kafka.inbound.QuitResponseRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class QuitResponseRecordHandler implements BaseRecordHandler<QuitResponseRecord> {

  private static final Logger log = LoggerFactory.getLogger(QuitResponseRecordHandler.class);

  @Override
  public void handleRecord(QuitResponseRecord record) {
    log.info("{} to offline userId: {}", record, record.userId());
  }
}
