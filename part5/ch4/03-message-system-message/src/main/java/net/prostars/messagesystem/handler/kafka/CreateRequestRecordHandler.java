package net.prostars.messagesystem.handler.kafka;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.constant.ResultType;
import net.prostars.messagesystem.dto.domain.Channel;
import net.prostars.messagesystem.dto.domain.UserId;
import net.prostars.messagesystem.dto.kafka.CreateRequestRecord;
import net.prostars.messagesystem.dto.kafka.CreateResponseRecord;
import net.prostars.messagesystem.dto.kafka.ErrorResponseRecord;
import net.prostars.messagesystem.dto.kafka.JoinNotificationRecord;
import net.prostars.messagesystem.service.ChannelService;
import net.prostars.messagesystem.service.ClientNotificationService;
import net.prostars.messagesystem.service.UserService;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("unused")
public class CreateRequestRecordHandler implements BaseRecordHandler<CreateRequestRecord> {

  private final ChannelService channelService;
  private final UserService userService;
  private final ClientNotificationService clientNotificationService;

  public CreateRequestRecordHandler(
      ChannelService channelService,
      UserService userService,
      ClientNotificationService clientNotificationService) {
    this.channelService = channelService;
    this.userService = userService;
    this.clientNotificationService = clientNotificationService;
  }

  @Override
  public void handleRecord(CreateRequestRecord record) {
    UserId senderUserId = record.userId();
    List<UserId> participantIds = userService.getUserIds(record.participantUsernames());
    if (participantIds.isEmpty()) {
      clientNotificationService.sendError(
          new ErrorResponseRecord(
              senderUserId, MessageType.CREATE_REQUEST, ResultType.NOT_FOUND.getMessage()));
      return;
    }

    Pair<Optional<Channel>, ResultType> result;
    try {
      result = channelService.create(senderUserId, participantIds, record.title());
    } catch (Exception ex) {
      clientNotificationService.sendError(
          new ErrorResponseRecord(
              senderUserId, MessageType.CREATE_REQUEST, ResultType.FAILED.getMessage()));
      return;
    }

    if (result.getFirst().isEmpty()) {
      clientNotificationService.sendError(
          new ErrorResponseRecord(
              senderUserId, MessageType.CREATE_REQUEST, result.getSecond().getMessage()));
      return;
    }

    Channel channel = result.getFirst().get();
    clientNotificationService.sendMessage(
        senderUserId, new CreateResponseRecord(senderUserId, channel.channelId(), channel.title()));
    participantIds.forEach(
        participantId ->
            CompletableFuture.runAsync(
                () ->
                    clientNotificationService.sendMessage(
                        participantId,
                        new JoinNotificationRecord(
                            participantId, channel.channelId(), channel.title()))));
  }
}
