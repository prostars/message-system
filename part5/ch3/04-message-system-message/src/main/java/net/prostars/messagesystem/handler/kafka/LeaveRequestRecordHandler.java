package net.prostars.messagesystem.handler.kafka;

import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.dto.domain.UserId;
import net.prostars.messagesystem.dto.kafka.ErrorResponseRecord;
import net.prostars.messagesystem.dto.kafka.LeaveRequestRecord;
import net.prostars.messagesystem.dto.kafka.LeaveResponseRecord;
import net.prostars.messagesystem.service.ChannelService;
import net.prostars.messagesystem.service.ClientNotificationService;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("unused")
public class LeaveRequestRecordHandler implements BaseRecordHandler<LeaveRequestRecord> {

  private final ChannelService channelService;
  private final ClientNotificationService clientNotificationService;

  public LeaveRequestRecordHandler(
      ChannelService channelService, ClientNotificationService clientNotificationService) {
    this.channelService = channelService;
    this.clientNotificationService = clientNotificationService;
  }

  @Override
  public void handleRecord(LeaveRequestRecord record) {
    UserId senderUserId = record.userId();

    if (channelService.leave(senderUserId)) {
      clientNotificationService.sendMessage(senderUserId, new LeaveResponseRecord(senderUserId));
    } else {
      clientNotificationService.sendError(
          new ErrorResponseRecord(senderUserId, MessageType.LEAVE_REQUEST, "Leave failed."));
    }
  }
}
