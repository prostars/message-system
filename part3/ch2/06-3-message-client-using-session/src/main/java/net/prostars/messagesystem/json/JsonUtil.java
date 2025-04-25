package net.prostars.messagesystem.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;

public class JsonUtil {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  public static <T> Optional<T> fromJson(String json, Class<T> clazz) {
    try {
      return Optional.of(objectMapper.readValue(json, clazz));
    } catch (Exception ex) {
      System.err.println("Failed JSON to Object: " + ex.getMessage());
      return Optional.empty();
    }
  }

  public static Optional<String> toJson(Object object) {
    try {
      return Optional.of(objectMapper.writeValueAsString(object));
    } catch (Exception ex) {
      System.err.println("Failed Object to JSON: " + ex.getMessage());
      return Optional.empty();
    }
  }
}
