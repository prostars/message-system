package net.prostars.messagesystem.handler.kafka;

import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.constant.UserConnectionStatus;
import net.prostars.messagesystem.dto.domain.UserId;
import net.prostars.messagesystem.dto.kafka.DisconnectRequestRecord;
import net.prostars.messagesystem.dto.kafka.DisconnectResponseRecord;
import net.prostars.messagesystem.dto.kafka.ErrorResponseRecord;
import net.prostars.messagesystem.service.ClientNotificationService;
import net.prostars.messagesystem.service.UserConnectionService;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("unused")
public class DisconnectRequestRecordHandler implements BaseRecordHandler<DisconnectRequestRecord> {

  private final UserConnectionService userConnectionService;
  private final ClientNotificationService clientNotificationService;

  public DisconnectRequestRecordHandler(
      UserConnectionService userConnectionService,
      ClientNotificationService clientNotificationService) {
    this.userConnectionService = userConnectionService;
    this.clientNotificationService = clientNotificationService;
  }

  @Override
  public void handleRecord(DisconnectRequestRecord record) {
    UserId senderUserId = record.userId();
    Pair<Boolean, String> result =
        userConnectionService.disconnect(senderUserId, record.username());
    if (result.getFirst()) {
      clientNotificationService.sendMessage(
          senderUserId,
          new DisconnectResponseRecord(
              senderUserId, record.username(), UserConnectionStatus.DISCONNECTED));
    } else {
      String errorMessage = result.getSecond();
      clientNotificationService.sendError(
          new ErrorResponseRecord(senderUserId, MessageType.DISCONNECT_REQUEST, errorMessage));
    }
  }
}
