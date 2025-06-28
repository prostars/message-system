package net.prostars.messagesystem.config;

import java.util.HashMap;
import java.util.Map;

import net.prostars.messagesystem.kafka.KafkaConsumerAwareRebalanceListener;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.producer.Producer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;

@Configuration
public class KafkaConfig {

  private static final Logger log = LoggerFactory.getLogger(KafkaConfig.class);

  @Value("${spring.kafka.bootstrap-servers}")
  private String bootstrapServers;

  @Bean
  public KafkaAdmin kafkaAdmin() {
    Map<String, Object> configs = new HashMap<>();
    configs.put("bootstrap.servers", bootstrapServers);
    configs.put(AdminClientConfig.RETRIES_CONFIG, 5);
    configs.put(AdminClientConfig.RETRY_BACKOFF_MS_CONFIG, 1000);
    return new KafkaAdmin(configs);
  }

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

  @Bean
  public KafkaTemplate<String, String> kafkaTemplate(
      DefaultKafkaProducerFactory<String, String> producerFactory) {
    Producer<String, String> producer = producerFactory.createProducer();
    producer.close();
    log.info("Kafka producer initialize.");
    return new KafkaTemplate<>(producerFactory);
  }
}
