package net.prostars.messagesystem.kafka;

import java.util.function.BiConsumer;
import net.prostars.messagesystem.dto.domain.ChannelId;
import net.prostars.messagesystem.dto.domain.UserId;
import net.prostars.messagesystem.dto.kafka.RecordInterface;
import net.prostars.messagesystem.json.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {

  private static final Logger log = LoggerFactory.getLogger(KafkaProducer.class);

  private final JsonUtil jsonUtil;
  private final KafkaTemplate<String, String> kafkaTemplate;
  private final String messageTopic;
  private final String requestTopic;
  private final String pushTopic;

  public KafkaProducer(
      JsonUtil jsonUtil,
      KafkaTemplate<String, String> kafkaTemplate,
      @Value("${message-system.kafka.topics.message}") String messageTopic,
      @Value("${message-system.kafka.topics.request}") String requestTopic,
      @Value("${message-system.kafka.topics.push}") String pushTopic) {
    this.jsonUtil = jsonUtil;
    this.kafkaTemplate = kafkaTemplate;
    this.messageTopic = messageTopic;
    this.requestTopic = requestTopic;
    this.pushTopic = pushTopic;
  }

  public void sendRequest(RecordInterface recordInterface, Runnable errorCallback) {
    jsonUtil
        .toJson(recordInterface)
        .ifPresent(
            record ->
                kafkaTemplate
                    .send(requestTopic, record)
                    .whenComplete(logResult(requestTopic, record, null, errorCallback)));
  }

  public void sendMessageUsingPartitionKey(
      ChannelId channelId, UserId userId, RecordInterface recordInterface, Runnable errorCallback) {
    String partitionKey = "%d-%d".formatted(channelId.id(), userId.id());
    jsonUtil
        .toJson(recordInterface)
        .ifPresent(
            record ->
                kafkaTemplate
                    .send(messageTopic, partitionKey, record)
                    .whenComplete(logResult(messageTopic, record, partitionKey, errorCallback)));
  }

  public void sendPushNotification(RecordInterface recordInterface) {
    jsonUtil
        .toJson(recordInterface)
        .ifPresent(
            record ->
                kafkaTemplate
                    .send(pushTopic, record)
                    .whenComplete(logResult(pushTopic, record, null, null)));
  }

  private BiConsumer<SendResult<String, String>, Throwable> logResult(
      String topic, String record, String partitionKey, Runnable errorCallback) {
    return (sendResult, throwable) -> {
      if (throwable == null) {
        log.info(
            "Record produced: {} with key: {} to topic: {}",
            sendResult.getProducerRecord().value(),
            partitionKey,
            sendResult.getProducerRecord().topic());
      } else {
        log.error(
            "Record producing failed: {} with key: {} to topic: {}, cause: {}",
            record,
            partitionKey,
            topic,
            throwable.getMessage());
        if (errorCallback != null) {
          errorCallback.run();
        }
      }
    };
  }
}
