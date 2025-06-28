package net.prostars.messagesystem.kafka;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.errors.TopicExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.stereotype.Service;

@Service
public class ListenTopicCreator {

  private static final Logger log = LoggerFactory.getLogger(ListenTopicCreator.class);

  private final KafkaAdmin kafkaAdmin;
  private final String prefixListenTopic;
  private final String prefixGroupId;
  private final String serverId;
  private final String partitions;
  private final String replicationFactor;

  public ListenTopicCreator(
      KafkaAdmin kafkaAdmin,
      @Value("${message-system.kafka.listeners.listen.prefix-topic}") String prefixListenTopic,
      @Value("${message-system.kafka.listeners.listen.prefix-group-id}") String prefixGroupId,
      @Value("${message-system.kafka.listeners.listen.partitions}") String partitions,
      @Value("${message-system.kafka.listeners.listen.replicationFactor}") String replicationFactor,
      @Value("${server.id}") String serverId) {
    this.kafkaAdmin = kafkaAdmin;
    this.prefixListenTopic = prefixListenTopic;
    this.prefixGroupId = prefixGroupId;
    this.serverId = serverId;
    this.partitions = partitions;
    this.replicationFactor = replicationFactor;
  }

  @PostConstruct
  public void init() {
    createTopic(
        getListenTopic(), Integer.parseInt(partitions), Short.parseShort(replicationFactor));
  }

  public void createTopic(String topicName, int partitions, short replicationFactor) {
    try (AdminClient adminClient = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
      NewTopic newTopic = new NewTopic(topicName, partitions, replicationFactor);
      CreateTopicsResult topicsResult = adminClient.createTopics(List.of(newTopic));
      topicsResult
          .values()
          .forEach(
              (topic, future) -> {
                try {
                  future.get();
                  log.info("Create topic: {}", topicName);
                } catch (ExecutionException ex) {
                  if (ex.getCause() instanceof TopicExistsException) {
                    log.info("Already existing topic: {}", topicName);
                  } else {
                    String message =
                        "Create topic failed. topic: %s, cause: %s"
                            .formatted(topicName, ex.getMessage());
                    log.error(message);
                    throw new RuntimeException(message, ex);
                  }
                } catch (InterruptedException ex) {
                  Thread.currentThread().interrupt();
                  throw new RuntimeException("Interrupt", ex);
                }
              });
    }
  }

  public String getListenTopic() {
    return "%s-%s".formatted(prefixListenTopic, serverId);
  }

  public String getConsumerGroupId() {
    return "%s-%s".formatted(prefixGroupId, serverId);
  }
}
