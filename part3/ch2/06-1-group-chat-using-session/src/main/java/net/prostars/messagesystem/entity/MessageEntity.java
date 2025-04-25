package net.prostars.messagesystem.entity;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "message")
public class MessageEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "message_sequence", nullable = false)
  private Long messageSequence;

  @Column(name = "user_name", nullable = false)
  private String username;

  @Column(name = "content", nullable = false)
  private String content;

  public MessageEntity() {}

  public MessageEntity(String username, String content) {
    this.username = username;
    this.content = content;
  }

  public Long getMessageSequence() {
    return messageSequence;
  }

  public String getUsername() {
    return username;
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
    return "MessageEntity{messageSequence=%d, username='%s', content='%s', createAt=%s, updatedAt=%s}"
        .formatted(messageSequence, username, content, getCreateAt(), getUpdatedAt());
  }
}
