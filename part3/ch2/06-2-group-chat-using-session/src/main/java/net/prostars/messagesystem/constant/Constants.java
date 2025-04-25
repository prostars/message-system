package net.prostars.messagesystem.constant;

public enum Constants {
  HTTP_SESSION_ID("HTTP_SESSION_ID");

  private final String value;

  Constants(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
