package net.prostars.messagesystem.handler.kafka;

import java.util.List;
import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.constant.ResultType;
import net.prostars.messagesystem.dto.domain.ChannelId;
import net.prostars.messagesystem.dto.domain.Message;
import net.prostars.messagesystem.dto.domain.UserId;
import net.prostars.messagesystem.dto.kafka.ErrorResponseRecord;
import net.prostars.messagesystem.dto.kafka.FetchMessagesRequestRecord;
import net.prostars.messagesystem.dto.kafka.FetchMessagesResponseRecord;
import net.prostars.messagesystem.service.ClientNotificationService;
import net.prostars.messagesystem.service.MessageService;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("unused")
public class FetchMessagesRequestRecordHandler
    implements BaseRecordHandler<FetchMessagesRequestRecord> {

  private final MessageService messageService;
  private final ClientNotificationService clientNotificationService;

  public FetchMessagesRequestRecordHandler(
      MessageService messageService, ClientNotificationService clientNotificationService) {
    this.messageService = messageService;
    this.clientNotificationService = clientNotificationService;
  }

  @Override
  public void handleRecord(FetchMessagesRequestRecord record) {
    UserId senderUserId = record.userId();
    ChannelId channelId = record.channelId();
    Pair<List<Message>, ResultType> result =
        messageService.getMessages(channelId, record.startMessageSeqId(), record.endMessageSeqId());
    if (result.getSecond() == ResultType.SUCCESS) {
      List<Message> messages = result.getFirst();
      clientNotificationService.sendMessageUsingPartitionKey(
          channelId,
          senderUserId,
          new FetchMessagesResponseRecord(senderUserId, channelId, messages));
    } else {
      clientNotificationService.sendError(
          new ErrorResponseRecord(
              senderUserId, MessageType.FETCH_MESSAGES_REQUEST, result.getSecond().getMessage()));
    }
  }
}
