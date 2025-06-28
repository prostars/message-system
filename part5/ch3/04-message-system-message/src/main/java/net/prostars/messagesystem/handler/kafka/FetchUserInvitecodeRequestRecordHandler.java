package net.prostars.messagesystem.handler.kafka;

import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.dto.domain.UserId;
import net.prostars.messagesystem.dto.kafka.ErrorResponseRecord;
import net.prostars.messagesystem.dto.kafka.FetchUserInvitecodeRequestRecord;
import net.prostars.messagesystem.dto.kafka.FetchUserInvitecodeResponseRecord;
import net.prostars.messagesystem.service.ClientNotificationService;
import net.prostars.messagesystem.service.UserService;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("unused")
public class FetchUserInvitecodeRequestRecordHandler
    implements BaseRecordHandler<FetchUserInvitecodeRequestRecord> {

  private final UserService userService;
  private final ClientNotificationService clientNotificationService;

  public FetchUserInvitecodeRequestRecordHandler(
      UserService userService, ClientNotificationService clientNotificationService) {
    this.userService = userService;
    this.clientNotificationService = clientNotificationService;
  }

  @Override
  public void handleRecord(FetchUserInvitecodeRequestRecord record) {
    UserId senderUserId = record.userId();
    userService
        .getInviteCode(senderUserId)
        .ifPresentOrElse(
            inviteCode ->
                clientNotificationService.sendMessage(
                    senderUserId, new FetchUserInvitecodeResponseRecord(senderUserId, inviteCode)),
            () ->
                clientNotificationService.sendError(
                    new ErrorResponseRecord(
                        senderUserId,
                        MessageType.FETCH_USER_INVITECODE_REQUEST,
                        "Fetch user invite code failed.")));
  }
}
