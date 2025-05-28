package net.prostars.messagesystem.service;

import net.prostars.messagesystem.dto.kafka.outbound.RecordInterface;
import net.prostars.messagesystem.json.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

  private static final Logger log = LoggerFactory.getLogger(KafkaProducerService.class);

  private final JsonUtil jsonUtil;
  private final KafkaTemplate<String, String> kafkaTemplate;
  private final String pushTopic;

  public KafkaProducerService(
      JsonUtil jsonUtil,
      KafkaTemplate<String, String> kafkaTemplate,
      @Value("${message-system.kafka.listeners.push.topic}") String pushTopic) {
    this.jsonUtil = jsonUtil;
    this.kafkaTemplate = kafkaTemplate;
    this.pushTopic = pushTopic;
  }

  public void sendPushNotification(RecordInterface recordInterface) {
    jsonUtil
        .toJson(recordInterface)
        .ifPresent(
            record ->
                kafkaTemplate
                    .send(pushTopic, record)
                    .whenComplete(
                        (sendResult, throwable) -> {
                          if (throwable == null) {
                            log.info(
                                "Record produced: {} to topic: {}",
                                sendResult.getProducerRecord().value(),
                                sendResult.getProducerRecord().topic());
                          } else {
                            log.error(
                                "Record producing failed: {} to topic: {}, cause: {}",
                                record,
                                pushTopic,
                                throwable.getMessage());
                          }
                        }));
  }
}
