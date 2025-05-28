package net.prostars.messagesystem.dto.domain;

import java.util.Objects;

public record User(UserId userId, String username) {

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    User user = (User) o;
    return Objects.equals(userId, user.userId);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(userId);
  }
}
