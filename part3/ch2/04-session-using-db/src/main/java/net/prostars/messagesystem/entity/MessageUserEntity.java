package net.prostars.messagesystem.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "message_user")
public class MessageUserEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "username", nullable = false)
  private String username;

  @Column(name = "password", nullable = false)
  private String password;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createAt;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  public MessageUserEntity() {}

  public MessageUserEntity(String username, String password) {
    this.username = username;
    this.password = password;
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

  public LocalDateTime getCreateAt() {
    return createAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  @PrePersist
  public void prePersist() {
    this.createAt = LocalDateTime.now();
    this.updatedAt = this.createAt;
  }

  @PreUpdate
  public void preUpdate() {
    this.updatedAt = LocalDateTime.now();
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    MessageUserEntity that = (MessageUserEntity) o;
    return Objects.equals(username, that.username);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(username);
  }

  @Override
  public String toString() {
    return "MessageUserEntity{userId=%d, username='%s', createAt=%s, updatedAt=%s}"
        .formatted(userId, username, createAt, updatedAt);
  }
}
