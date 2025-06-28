package net.prostars.messagesystem.handler.kafka;

import java.util.Optional;
import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.constant.UserConnectionStatus;
import net.prostars.messagesystem.dto.domain.UserId;
import net.prostars.messagesystem.dto.kafka.ErrorResponseRecord;
import net.prostars.messagesystem.dto.kafka.InviteNotificationRecord;
import net.prostars.messagesystem.dto.kafka.InviteRequestRecord;
import net.prostars.messagesystem.dto.kafka.InviteResponseRecord;
import net.prostars.messagesystem.service.ClientNotificationService;
import net.prostars.messagesystem.service.UserConnectionService;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("unused")
public class InviteRequestRecordHandler implements BaseRecordHandler<InviteRequestRecord> {

  private final UserConnectionService userConnectionService;
  private final ClientNotificationService clientNotificationService;

  public InviteRequestRecordHandler(
      UserConnectionService userConnectionService,
      ClientNotificationService clientNotificationService) {
    this.userConnectionService = userConnectionService;
    this.clientNotificationService = clientNotificationService;
  }

  @Override
  public void handleRecord(InviteRequestRecord record) {
    UserId inviterUserId = record.userId();
    Pair<Optional<UserId>, String> result =
        userConnectionService.invite(inviterUserId, record.userInviteCode());
    result
        .getFirst()
        .ifPresentOrElse(
            partnerUserId -> {
              String inviterUsername = result.getSecond();
              clientNotificationService.sendMessage(
                  inviterUserId,
                  new InviteResponseRecord(
                      inviterUserId, record.userInviteCode(), UserConnectionStatus.PENDING));
              clientNotificationService.sendMessage(
                  partnerUserId, new InviteNotificationRecord(partnerUserId, inviterUsername));
            },
            () -> {
              String errorMessage = result.getSecond();
              clientNotificationService.sendError(
                  new ErrorResponseRecord(inviterUserId, MessageType.INVITE_REQUEST, errorMessage));
            });
  }
}
