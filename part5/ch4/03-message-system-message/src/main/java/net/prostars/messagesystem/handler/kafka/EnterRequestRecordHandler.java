package net.prostars.messagesystem.handler.kafka;

import java.util.Optional;
import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.constant.ResultType;
import net.prostars.messagesystem.dto.domain.ChannelEntry;
import net.prostars.messagesystem.dto.domain.UserId;
import net.prostars.messagesystem.dto.kafka.EnterRequestRecord;
import net.prostars.messagesystem.dto.kafka.EnterResponseRecord;
import net.prostars.messagesystem.dto.kafka.ErrorResponseRecord;
import net.prostars.messagesystem.service.ChannelService;
import net.prostars.messagesystem.service.ClientNotificationService;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("unused")
public class EnterRequestRecordHandler implements BaseRecordHandler<EnterRequestRecord> {

  private final ChannelService channelService;
  private final ClientNotificationService clientNotificationService;

  public EnterRequestRecordHandler(
      ChannelService channelService, ClientNotificationService clientNotificationService) {
    this.channelService = channelService;
    this.clientNotificationService = clientNotificationService;
  }

  @Override
  public void handleRecord(EnterRequestRecord record) {
    UserId senderUserId = record.userId();

    Pair<Optional<ChannelEntry>, ResultType> result =
        channelService.enter(record.channelId(), senderUserId);
    result
        .getFirst()
        .ifPresentOrElse(
            channelEntry ->
                clientNotificationService.sendMessage(
                    senderUserId,
                    new EnterResponseRecord(
                        senderUserId,
                        record.channelId(),
                        channelEntry.title(),
                        channelEntry.lastReadMessageSeqId(),
                        channelEntry.lastChannelMessageSeqId())),
            () ->
                clientNotificationService.sendError(
                    new ErrorResponseRecord(
                        senderUserId, MessageType.ENTER_REQUEST, result.getSecond().getMessage())));
  }
}
