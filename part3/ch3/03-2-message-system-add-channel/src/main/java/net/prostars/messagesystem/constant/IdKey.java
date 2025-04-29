package net.prostars.messagesystem.constant;

public enum IdKey {
  HTTP_SESSION_ID("HTTP_SESSION_ID"),
  USER_ID("USER_ID"),
  CHANNEL_ID("channel_id");

  private final String value;

  IdKey(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
