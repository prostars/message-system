package net.prostars.messagesystem.handler.kafka;

import java.util.Optional;
import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.dto.domain.UserId;
import net.prostars.messagesystem.dto.kafka.AcceptNotificationRecord;
import net.prostars.messagesystem.dto.kafka.AcceptRequestRecord;
import net.prostars.messagesystem.dto.kafka.AcceptResponseRecord;
import net.prostars.messagesystem.dto.kafka.ErrorResponseRecord;
import net.prostars.messagesystem.service.ClientNotificationService;
import net.prostars.messagesystem.service.UserConnectionService;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("unused")
public class AcceptRequestRecordHandler implements BaseRecordHandler<AcceptRequestRecord> {

  private final UserConnectionService userConnectionService;
  private final ClientNotificationService clientNotificationService;

  public AcceptRequestRecordHandler(
      UserConnectionService userConnectionService,
      ClientNotificationService clientNotificationService) {
    this.userConnectionService = userConnectionService;
    this.clientNotificationService = clientNotificationService;
  }

  @Override
  public void handleRecord(AcceptRequestRecord record) {
    UserId acceptorUserId = record.userId();
    Pair<Optional<UserId>, String> result =
        userConnectionService.accept(acceptorUserId, record.username());
    result
        .getFirst()
        .ifPresentOrElse(
            inviterUserId -> {
              clientNotificationService.sendMessage(
                  acceptorUserId, new AcceptResponseRecord(acceptorUserId, record.username()));
              String acceptorUsername = result.getSecond();
              clientNotificationService.sendMessage(
                  inviterUserId, new AcceptNotificationRecord(inviterUserId, acceptorUsername));
            },
            () -> {
              String errorMessage = result.getSecond();
              clientNotificationService.sendError(
                  new ErrorResponseRecord(
                      acceptorUserId, MessageType.ACCEPT_REQUEST, errorMessage));
            });
  }
}
