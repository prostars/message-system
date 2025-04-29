package net.prostars.messagesystem.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class JsonUtil {

  private static final Logger log = LoggerFactory.getLogger(JsonUtil.class);

  private final ObjectMapper objectMapper;

  public JsonUtil(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  public <T> Optional<T> fromJson(String json, Class<T> clazz) {
    try {
      return Optional.of(objectMapper.readValue(json, clazz));
    } catch (Exception ex) {
      log.error("Failed JSON to Object: {}", ex.getMessage());
      return Optional.empty();
    }
  }

  public Optional<String> toJson(Object object) {
    try {
      return Optional.of(objectMapper.writeValueAsString(object));
    } catch (Exception ex) {
      log.error("Failed Object to JSON: {}", ex.getMessage());
      return Optional.empty();
    }
  }
}
