package net.prostars.messagesystem.service;

import java.util.HashMap;
import net.prostars.messagesystem.dto.domain.UserId;
import net.prostars.messagesystem.dto.kafka.outbound.RecordInterface;
import net.prostars.messagesystem.json.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PushService {

  private static final Logger log = LoggerFactory.getLogger(PushService.class);

  private final KafkaProducerService kafkaProducerService;
  private final JsonUtil jsonUtil;
  private final HashMap<String, Class<? extends RecordInterface>> pushMessageTypes =
      new HashMap<>();

  public PushService(KafkaProducerService kafkaProducerService, JsonUtil jsonUtil) {
    this.kafkaProducerService = kafkaProducerService;
    this.jsonUtil = jsonUtil;
  }

  public void registerPushMessageType(String messageType, Class<? extends RecordInterface> clazz) {
    pushMessageTypes.put(messageType, clazz);
  }

  public void pushMessage(UserId userId, String messageType, String message) {
    Class<? extends RecordInterface> recordInterface = pushMessageTypes.get(messageType);
    if (recordInterface != null) {
      jsonUtil
          .addValue(message, "userId", userId.id().toString())
          .flatMap(json -> jsonUtil.fromJson(json, recordInterface))
          .ifPresent(kafkaProducerService::sendPushNotification);
      log.info("push message: {} to user: {}", message, userId);
    } else {
      log.error("Invalid message type: {}", messageType);
    }
  }
}
