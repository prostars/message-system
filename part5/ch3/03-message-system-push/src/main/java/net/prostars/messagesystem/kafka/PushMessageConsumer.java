package net.prostars.messagesystem.kafka;

import net.prostars.messagesystem.dto.kafka.inbound.RecordInterface;
import net.prostars.messagesystem.handler.kafka.RecordDispatcher;
import net.prostars.messagesystem.json.JsonUtil;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
public class PushMessageConsumer {

  private static final Logger log = LoggerFactory.getLogger(PushMessageConsumer.class);

  private final RecordDispatcher recordDispatcher;
  private final JsonUtil jsonUtil;

  public PushMessageConsumer(RecordDispatcher recordDispatcher, JsonUtil jsonUtil) {
    this.recordDispatcher = recordDispatcher;
    this.jsonUtil = jsonUtil;
  }

  @KafkaListener(
      topics = "${message-system.kafka.listeners.push.topic}",
      groupId = "${message-system.kafka.listeners.push.group-id}",
      concurrency = "${message-system.kafka.listeners.push.concurrency}")
  public void consumeMessage(
      ConsumerRecord<String, String> consumerRecord, Acknowledgment acknowledgment) {
    try {
      log.info(
          "Received record: {}, from topic: {}, on key: {}, partition: {}, offset: {}",
          consumerRecord.value(),
          consumerRecord.topic(),
          consumerRecord.key(),
          consumerRecord.partition(),
          consumerRecord.offset());
      jsonUtil
          .fromJson(consumerRecord.value(), RecordInterface.class)
          .ifPresent(recordDispatcher::dispatchRecord);
    } catch (Exception ex) {
      log.error("Record handling failed. cause: {}", ex.getMessage());
    } finally {
      acknowledgment.acknowledge();
    }
  }
}
