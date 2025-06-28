package net.prostars.messagesystem.handler.kafka;

import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.constant.UserConnectionStatus;
import net.prostars.messagesystem.dto.domain.UserId;
import net.prostars.messagesystem.dto.kafka.ErrorResponseRecord;
import net.prostars.messagesystem.dto.kafka.RejectRequestRecord;
import net.prostars.messagesystem.dto.kafka.RejectResponseRecord;
import net.prostars.messagesystem.service.ClientNotificationService;
import net.prostars.messagesystem.service.UserConnectionService;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("unused")
public class RejectRequestRecordHandler implements BaseRecordHandler<RejectRequestRecord> {

  private final UserConnectionService userConnectionService;
  private final ClientNotificationService clientNotificationService;

  public RejectRequestRecordHandler(
      UserConnectionService userConnectionService,
      ClientNotificationService clientNotificationService) {
    this.userConnectionService = userConnectionService;
    this.clientNotificationService = clientNotificationService;
  }

  @Override
  public void handleRecord(RejectRequestRecord record) {
    UserId senderUserId = record.userId();
    Pair<Boolean, String> result = userConnectionService.reject(senderUserId, record.username());
    if (result.getFirst()) {
      clientNotificationService.sendMessage(
          senderUserId,
          new RejectResponseRecord(senderUserId, record.username(), UserConnectionStatus.REJECTED));
    } else {
      String errorMessage = result.getSecond();
      clientNotificationService.sendError(
          new ErrorResponseRecord(senderUserId, MessageType.REJECT_REQUEST, errorMessage));
    }
  }
}
