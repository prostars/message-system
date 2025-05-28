package net.prostars.messagesystem.config;

import org.apache.kafka.clients.producer.Producer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class KafkaConfig {

  private static final Logger log = LoggerFactory.getLogger(KafkaConfig.class);

  @Bean
  public KafkaTemplate<String, String> kafkaTemplate(
      DefaultKafkaProducerFactory<String, String> producerFactory) {
    Producer<String, String> producer = producerFactory.createProducer();
    producer.close();
    log.info("Kafka producer initialize.");
    return new KafkaTemplate<>(producerFactory);
  }
}
