package net.prostars.messagesystem.service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import net.prostars.messagesystem.dto.domain.ChannelId;
import net.prostars.messagesystem.dto.domain.UserId;
import net.prostars.messagesystem.entity.MessageEntity;
import net.prostars.messagesystem.repository.MessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

  private static final Logger log = LoggerFactory.getLogger(MessageService.class);
  private static final int THREAD_POOL_SIZE = 10;

  private final ChannelService channelService;
  private final MessageRepository messageRepository;
  private final ExecutorService senderThreadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

  public MessageService(ChannelService channelService, MessageRepository messageRepository) {
    this.channelService = channelService;
    this.messageRepository = messageRepository;
  }

  public void sendMessage(
      UserId senderUserId, String content, ChannelId channelId, Consumer<UserId> messageSender) {
    try {
      messageRepository.save(new MessageEntity(senderUserId.id(), content));
    } catch (Exception ex) {
      log.error("Send message failed. cause: {}", ex.getMessage());
      return;
    }

    channelService.getOnlineParticipantIds(channelId).stream()
        .filter(participantId -> !senderUserId.equals(participantId))
        .forEach(
            participantId ->
                CompletableFuture.runAsync(
                    () -> messageSender.accept(participantId), senderThreadPool));
  }
}
