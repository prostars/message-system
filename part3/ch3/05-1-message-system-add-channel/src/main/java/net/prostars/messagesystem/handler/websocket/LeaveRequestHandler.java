package net.prostars.messagesystem.handler.websocket;

import net.prostars.messagesystem.constant.IdKey;
import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.dto.domain.UserId;
import net.prostars.messagesystem.dto.websocket.inbound.LeaveRequest;
import net.prostars.messagesystem.dto.websocket.outbound.ErrorResponse;
import net.prostars.messagesystem.dto.websocket.outbound.LeaveResponse;
import net.prostars.messagesystem.service.ChannelService;
import net.prostars.messagesystem.session.WebSocketSessionManager;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
@SuppressWarnings("unused")
public class LeaveRequestHandler implements BaseRequestHandler<LeaveRequest> {

  private final ChannelService channelService;
  private final WebSocketSessionManager webSocketSessionManager;

  public LeaveRequestHandler(
      ChannelService channelService, WebSocketSessionManager webSocketSessionManager) {
    this.channelService = channelService;
    this.webSocketSessionManager = webSocketSessionManager;
  }

  @Override
  public void handleRequest(WebSocketSession senderSession, LeaveRequest request) {
    UserId senderUserId = (UserId) senderSession.getAttributes().get(IdKey.USER_ID.getValue());

    if (channelService.leave(senderUserId)) {
      webSocketSessionManager.sendMessage(senderSession, new LeaveResponse());
    } else {
      webSocketSessionManager.sendMessage(
          senderSession, new ErrorResponse(MessageType.LEAVE_REQUEST, "Leave failed."));
    }
  }
}
