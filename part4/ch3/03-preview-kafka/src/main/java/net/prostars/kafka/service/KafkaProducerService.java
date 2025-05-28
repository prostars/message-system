package net.prostars.kafka.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

  private static final Logger log = LoggerFactory.getLogger(KafkaProducerService.class);

  private final KafkaTemplate<String, String> kafkaTemplate;

  public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  public void sendMessage(String topic, String key, String message) {
    SendResult<String, String> sendResult;
    try {
      if (key == null || key.isEmpty()) {
        sendResult = kafkaTemplate.send(topic, message).get();
      } else {
        sendResult = kafkaTemplate.send(topic, key, message).get();
      }
      log.info("Send result: {}", sendResult);
    } catch (Exception ex) {
      log.error(
          "Send failed: {} to topic: {} key: {} cause: {}", message, topic, key, ex.getMessage());
    }
  }
}
