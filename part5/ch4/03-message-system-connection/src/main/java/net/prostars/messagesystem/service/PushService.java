package net.prostars.messagesystem.service;

import java.util.HashMap;
import net.prostars.messagesystem.dto.kafka.RecordInterface;
import net.prostars.messagesystem.kafka.KafkaProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PushService {

  private static final Logger log = LoggerFactory.getLogger(PushService.class);

  private final KafkaProducer kafkaProducer;
  private final HashMap<String, Class<? extends RecordInterface>> pushMessageTypes =
      new HashMap<>();

  public PushService(KafkaProducer kafkaProducer) {
    this.kafkaProducer = kafkaProducer;
  }

  public void registerPushMessageType(String messageType, Class<? extends RecordInterface> clazz) {
    pushMessageTypes.put(messageType, clazz);
  }

  public void pushMessage(RecordInterface recordInterface) {
    String messageType = recordInterface.type();
    if (pushMessageTypes.containsKey(messageType)) {
      kafkaProducer.sendPushNotification(recordInterface);
    } else {
      log.error("Invalid message type: {}", messageType);
    }
  }
}
