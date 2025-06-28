package net.prostars.messagesystem.dto.domain;

public record UserId(Long id) {

  public UserId {
    if (id == null || id <= 0) {
      throw new IllegalArgumentException("Invalid UserId");
    }
  }
}
