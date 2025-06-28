package net.prostars.messagesystem.handler.websocket;

import net.prostars.messagesystem.constant.IdKey;
import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.constant.ResultType;
import net.prostars.messagesystem.dto.domain.UserId;
import net.prostars.messagesystem.dto.websocket.inbound.QuitRequest;
import net.prostars.messagesystem.dto.websocket.outbound.ErrorResponse;
import net.prostars.messagesystem.dto.websocket.outbound.QuitResponse;
import net.prostars.messagesystem.service.ChannelService;
import net.prostars.messagesystem.service.ClientNotificationService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
@SuppressWarnings("unused")
public class QuitRequestHandler implements BaseRequestHandler<QuitRequest> {

  private final ChannelService channelService;
  private final ClientNotificationService clientNotificationService;

  public QuitRequestHandler(
      ChannelService channelService, ClientNotificationService clientNotificationService) {
    this.channelService = channelService;
    this.clientNotificationService = clientNotificationService;
  }

  @Override
  public void handleRequest(WebSocketSession senderSession, QuitRequest request) {
    UserId senderUserId = (UserId) senderSession.getAttributes().get(IdKey.USER_ID.getValue());

    ResultType result;
    try {
      result = channelService.quit(request.getChannelId(), senderUserId);
    } catch (Exception ex) {
      clientNotificationService.sendMessage(
          senderSession,
          senderUserId,
          new ErrorResponse(MessageType.QUIT_REQUEST, ResultType.FAILED.getMessage()));
      return;
    }

    if (result == ResultType.SUCCESS) {
      clientNotificationService.sendMessage(
          senderSession, senderUserId, new QuitResponse(request.getChannelId()));
    } else {
      clientNotificationService.sendMessage(
          senderSession,
          senderUserId,
          new ErrorResponse(MessageType.QUIT_REQUEST, result.getMessage()));
    }
  }
}
