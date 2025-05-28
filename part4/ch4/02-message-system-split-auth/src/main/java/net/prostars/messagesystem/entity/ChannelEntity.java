package net.prostars.messagesystem.entity;

import jakarta.persistence.*;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "channel")
public class ChannelEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "channel_id")
  private Long channelId;

  @Column(name = "title", nullable = false)
  private String title;

  @Column(name = "invite_code", nullable = false)
  private String inviteCode;

  @Column(name = "head_count", nullable = false)
  private int headCount;

  public ChannelEntity() {}

  public ChannelEntity(String title, int headCount) {
    this.title = title;
    this.headCount = headCount;
    this.inviteCode = UUID.randomUUID().toString().replace("-", "");
  }

  public Long getChannelId() {
    return channelId;
  }

  public String getTitle() {
    return title;
  }

  public String getInviteCode() {
    return inviteCode;
  }

  public int getHeadCount() {
    return headCount;
  }

  public void setHeadCount(int headCount) {
    this.headCount = headCount;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    ChannelEntity that = (ChannelEntity) o;
    return Objects.equals(channelId, that.channelId);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(channelId);
  }

  @Override
  public String toString() {
    return "ChannelEntity{channelId=%d, title='%s', inviteCode='%s', headCount=%d}"
        .formatted(channelId, title, inviteCode, headCount);
  }
}
