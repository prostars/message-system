package net.prostars.messagesystem.handler.kafka;

import java.util.Optional;
import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.constant.ResultType;
import net.prostars.messagesystem.dto.domain.Channel;
import net.prostars.messagesystem.dto.domain.UserId;
import net.prostars.messagesystem.dto.kafka.ErrorResponseRecord;
import net.prostars.messagesystem.dto.kafka.JoinRequestRecord;
import net.prostars.messagesystem.dto.kafka.JoinResponseRecord;
import net.prostars.messagesystem.service.ChannelService;
import net.prostars.messagesystem.service.ClientNotificationService;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("unused")
public class JoinRequestRecordHandler implements BaseRecordHandler<JoinRequestRecord> {

  private final ChannelService channelService;
  private final ClientNotificationService clientNotificationService;

  public JoinRequestRecordHandler(
      ChannelService channelService, ClientNotificationService clientNotificationService) {
    this.channelService = channelService;
    this.clientNotificationService = clientNotificationService;
  }

  @Override
  public void handleRecord(JoinRequestRecord record) {
    UserId senderUserId = record.userId();

    Pair<Optional<Channel>, ResultType> result;
    try {
      result = channelService.join(record.inviteCode(), senderUserId);
    } catch (Exception ex) {
      clientNotificationService.sendError(
          new ErrorResponseRecord(
              senderUserId, MessageType.JOIN_REQUEST, ResultType.FAILED.getMessage()));
      return;
    }

    result
        .getFirst()
        .ifPresentOrElse(
            channel ->
                clientNotificationService.sendMessage(
                    senderUserId,
                    new JoinResponseRecord(senderUserId, channel.channelId(), channel.title())),
            () ->
                clientNotificationService.sendError(
                    new ErrorResponseRecord(
                        senderUserId, MessageType.JOIN_REQUEST, result.getSecond().getMessage())));
  }
}
