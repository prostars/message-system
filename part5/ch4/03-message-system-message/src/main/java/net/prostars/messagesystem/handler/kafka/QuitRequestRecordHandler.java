package net.prostars.messagesystem.handler.kafka;

import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.constant.ResultType;
import net.prostars.messagesystem.dto.domain.UserId;
import net.prostars.messagesystem.dto.kafka.ErrorResponseRecord;
import net.prostars.messagesystem.dto.kafka.QuitRequestRecord;
import net.prostars.messagesystem.dto.kafka.QuitResponseRecord;
import net.prostars.messagesystem.service.ChannelService;
import net.prostars.messagesystem.service.ClientNotificationService;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("unused")
public class QuitRequestRecordHandler implements BaseRecordHandler<QuitRequestRecord> {

  private final ChannelService channelService;
  private final ClientNotificationService clientNotificationService;

  public QuitRequestRecordHandler(
      ChannelService channelService, ClientNotificationService clientNotificationService) {
    this.channelService = channelService;
    this.clientNotificationService = clientNotificationService;
  }

  @Override
  public void handleRecord(QuitRequestRecord record) {
    UserId senderUserId = record.userId();

    ResultType result;
    try {
      result = channelService.quit(record.channelId(), senderUserId);
    } catch (Exception ex) {
      clientNotificationService.sendError(
          new ErrorResponseRecord(
              senderUserId, MessageType.QUIT_REQUEST, ResultType.FAILED.getMessage()));
      return;
    }

    if (result == ResultType.SUCCESS) {
      clientNotificationService.sendMessage(
          senderUserId, new QuitResponseRecord(senderUserId, record.channelId()));
    } else {
      clientNotificationService.sendError(
          new ErrorResponseRecord(senderUserId, MessageType.QUIT_REQUEST, result.getMessage()));
    }
  }
}
