package net.prostars.messagesystem.entity;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "message")
public class MessageEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "message_sequence")
  private Long messageSequence;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "content", nullable = false)
  private String content;

  public MessageEntity() {}

  public MessageEntity(Long userId, String content) {
    this.userId = userId;
    this.content = content;
  }

  public Long getMessageSequence() {
    return messageSequence;
  }

  public Long getUserId() {
    return userId;
  }

  public String getContent() {
    return content;
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
    return "MessageEntity{messageSequence=%d, userId=%d, content='%s', createAt=%s, updatedAt=%s}"
        .formatted(messageSequence, userId, content, getCreateAt(), getUpdatedAt());
  }
}
