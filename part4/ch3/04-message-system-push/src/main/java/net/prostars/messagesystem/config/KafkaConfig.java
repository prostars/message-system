package net.prostars.messagesystem.config;

import net.prostars.messagesystem.kafka.KafkaConsumerAwareRebalanceListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;

@Configuration
public class KafkaConfig {

  private static final Logger log = LoggerFactory.getLogger(KafkaConfig.class);

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(
      ConsumerFactory<String, String> consumerFactory,
      KafkaConsumerAwareRebalanceListener awareRebalanceListener) {
    ConcurrentKafkaListenerContainerFactory<String, String> containerFactory =
        new ConcurrentKafkaListenerContainerFactory<>();
    containerFactory.setConsumerFactory(consumerFactory);
    containerFactory
        .getContainerProperties()
        .setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
    containerFactory.getContainerProperties().setConsumerRebalanceListener(awareRebalanceListener);

    log.info("Set AckMode: {}", containerFactory.getContainerProperties().getAckMode());
    return containerFactory;
  }
}
