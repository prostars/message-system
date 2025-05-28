package net.prostars.kafka.controller;

import net.prostars.kafka.service.KafkaProducerService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/kafka")
public class KafkaController {

  private final KafkaProducerService producerService;

  public KafkaController(KafkaProducerService producerService) {
    this.producerService = producerService;
  }

  @PostMapping("/send")
  public void sendMessage(
      @RequestParam String topic,
      @RequestParam(required = false) String key,
      @RequestParam String message) {
    producerService.sendMessage(topic, key, message);
  }
}
