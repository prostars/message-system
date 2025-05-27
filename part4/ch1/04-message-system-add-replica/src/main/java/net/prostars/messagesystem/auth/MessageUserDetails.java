package net.prostars.messagesystem.auth;

import com.fasterxml.jackson.annotation.*;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageUserDetails implements UserDetails {

  private final Long userId;
  private final String username;
  private String password;

  @JsonCreator
  public MessageUserDetails(
      @JsonProperty("userId") Long userId,
      @JsonProperty("username") String username,
      @JsonProperty("password") String password) {
    this.userId = userId;
    this.username = username;
    this.password = password;
  }

  public Long getUserId() {
    return userId;
  }
  
  public void erasePassword() {
    password = "";
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
  @JsonIgnore
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
