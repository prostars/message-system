package net.prostars.messagesystem.auth;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class MessageUserDetails implements UserDetails {

  private final Long userId;
  private final String username;
  private final String password;

  public MessageUserDetails(Long userId, String username, String password) {
    this.userId = userId;
    this.username = username;
    this.password = password;
  }

  public Long getUserId() {
    return userId;
  }

  @Override
  public String getUsername() {
    return username;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of();
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    MessageUserDetails that = (MessageUserDetails) o;
    return Objects.equals(username, that.username);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(username);
  }
}
