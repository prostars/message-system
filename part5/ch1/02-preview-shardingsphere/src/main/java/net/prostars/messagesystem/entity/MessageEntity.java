package net.prostars.messagesystem.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "message")
public class MessageEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "message_sequence", nullable = false)
  private Long messageSequence;

  @Column(name = "channel_id", nullable = false)
  private Long channelId;

  @Column(name = "user_name", nullable = false)
  private String username;

  @Column(name = "content", nullable = false)
  private String content;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createAt;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  public MessageEntity() {}

  public MessageEntity(Long channelId, String username, String content) {
    this.channelId = channelId;
    this.username = username;
    this.content = content;
  }

  public Long getMessageSequence() {
    return messageSequence;
  }

  public Long getChannelId() {
    return channelId;
  }

  public String getUsername() {
    return username;
  }

  public String getContent() {
    return content;
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
    MessageEntity that = (MessageEntity) o;
    return Objects.equals(messageSequence, that.messageSequence);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(messageSequence);
  }

  @Override
  public String toString() {
    return "MessageEntity{messageSequence=%d, channelId=%d, username='%s', content='%s', createAt=%s, updatedAt=%s}"
        .formatted(messageSequence, channelId, username, content, createAt, updatedAt);
  }
}
