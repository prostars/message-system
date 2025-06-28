package net.prostars.messagesystem.service;

import java.util.List;
import net.prostars.messagesystem.database.ShardContext;
import net.prostars.messagesystem.dto.domain.ChannelId;
import net.prostars.messagesystem.dto.domain.MessageSeqId;
import net.prostars.messagesystem.dto.domain.UserId;
import net.prostars.messagesystem.dto.projection.MessageInfoProjection;
import net.prostars.messagesystem.entity.MessageEntity;
import net.prostars.messagesystem.repository.MessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MessageShardService {

  private final MessageRepository messageRepository;

  public MessageShardService(MessageRepository messageRepository) {
    this.messageRepository = messageRepository;
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
  public MessageSeqId findLastMessageSequenceByChannelId(ChannelId channelId) {
    try (ShardContext.ShardContextScope ignored =
        new ShardContext.ShardContextScope(channelId.id())) {
      return messageRepository
          .findLastMessageSequenceByChannelId(channelId.id())
          .map(MessageSeqId::new)
          .orElse(new MessageSeqId(0L));
    }
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
  public List<MessageInfoProjection> findByChannelIdAndMessageSequenceBetween(
      ChannelId channelId, MessageSeqId startMessageSeqId, MessageSeqId endMessageSeqId) {
    try (ShardContext.ShardContextScope ignored =
        new ShardContext.ShardContextScope(channelId.id())) {
      return messageRepository.findByChannelIdAndMessageSequenceBetween(
          channelId.id(), startMessageSeqId.id(), endMessageSeqId.id());
    }
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void save(
      ChannelId channelId, MessageSeqId messageSeqId, UserId senderUserId, String content) {
    try (ShardContext.ShardContextScope ignored =
        new ShardContext.ShardContextScope(channelId.id())) {
      messageRepository.save(
          new MessageEntity(channelId.id(), messageSeqId.id(), senderUserId.id(), content));
    }
  }
}
