package net.prostars.messagesystem.handler.websocket;

import net.prostars.messagesystem.constant.IdKey;
import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.dto.domain.UserId;
import net.prostars.messagesystem.dto.websocket.inbound.LeaveRequest;
import net.prostars.messagesystem.dto.websocket.outbound.ErrorResponse;
import net.prostars.messagesystem.dto.websocket.outbound.LeaveResponse;
import net.prostars.messagesystem.service.ChannelService;
import net.prostars.messagesystem.service.ClientNotificationService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
@SuppressWarnings("unused")
public class LeaveRequestHandler implements BaseRequestHandler<LeaveRequest> {

  private final ChannelService channelService;
  private final ClientNotificationService clientNotificationService;

  public LeaveRequestHandler(
      ChannelService channelService, ClientNotificationService clientNotificationService) {
    this.channelService = channelService;
    this.clientNotificationService = clientNotificationService;
  }

  @Override
  public void handleRequest(WebSocketSession senderSession, LeaveRequest request) {
    UserId senderUserId = (UserId) senderSession.getAttributes().get(IdKey.USER_ID.getValue());

    if (channelService.leave(senderUserId)) {
      clientNotificationService.sendMessage(senderSession, senderUserId, new LeaveResponse());
    } else {
      clientNotificationService.sendMessage(
          senderSession,
          senderUserId,
          new ErrorResponse(MessageType.LEAVE_REQUEST, "Leave failed."));
    }
  }
}
