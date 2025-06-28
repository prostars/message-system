package net.prostars.messagesystem.handler.kafka;

import net.prostars.messagesystem.dto.kafka.inbound.AcceptResponseRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AcceptResponseRecordHandler implements BaseRecordHandler<AcceptResponseRecord> {

  private static final Logger log = LoggerFactory.getLogger(AcceptResponseRecordHandler.class);

  @Override
  public void handleRecord(AcceptResponseRecord record) {
    log.info("{} to offline userId: {}", record, record.userId());
  }
}
