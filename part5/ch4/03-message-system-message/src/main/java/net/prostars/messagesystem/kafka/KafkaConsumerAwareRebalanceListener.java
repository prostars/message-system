package net.prostars.messagesystem.kafka;

import java.util.Collection;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.listener.ConsumerAwareRebalanceListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumerAwareRebalanceListener implements ConsumerAwareRebalanceListener {

  private static final Logger log =
      LoggerFactory.getLogger(KafkaConsumerAwareRebalanceListener.class);

  @Override
  public void onPartitionsAssigned(Consumer<?, ?> consumer, Collection<TopicPartition> partitions) {
    log.info("Kafka consumer {} assigned: {}", consumer.toString(), partitions.toString());
  }

  @Override
  public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
    log.info("Kafka consumer revoked: {}", partitions.toString());
  }
}
