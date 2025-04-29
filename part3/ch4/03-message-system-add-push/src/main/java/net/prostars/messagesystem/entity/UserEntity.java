package net.prostars.messagesystem.entity;

import jakarta.persistence.*;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "message_user")
public class UserEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_id")
  private Long userId;

  @Column(name = "username", nullable = false)
  private String username;

  @Column(name = "password", nullable = false)
  private String password;

  @Column(name = "invite_code", nullable = false)
  private String inviteCode;

  @Column(name = "connection_count", nullable = false)
  private int connectionCount;

  public UserEntity() {}

  public UserEntity(String username, String password) {
    this.username = username;
    this.password = password;
    this.inviteCode = UUID.randomUUID().toString().replace("-", "");
  }

  public Long getUserId() {
    return userId;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public String getInviteCode() {
    return inviteCode;
  }

  public int getConnectionCount() {
    return connectionCount;
  }

  public void setConnectionCount(int connectionCount) {
    this.connectionCount = connectionCount;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    UserEntity that = (UserEntity) o;
    return Objects.equals(username, that.username);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(username);
  }

  @Override
  public String toString() {
    return "MessageUserEntity{userId=%d, username='%s', createAt=%s, updatedAt=%s}"
        .formatted(userId, username, getCreateAt(), getUpdatedAt());
  }
}
