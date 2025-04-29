package net.prostars.messagesystem.handler.websocket;

import java.util.Optional;
import net.prostars.messagesystem.constant.IdKey;
import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.constant.ResultType;
import net.prostars.messagesystem.dto.domain.Channel;
import net.prostars.messagesystem.dto.domain.UserId;
import net.prostars.messagesystem.dto.websocket.inbound.JoinRequest;
import net.prostars.messagesystem.dto.websocket.outbound.ErrorResponse;
import net.prostars.messagesystem.dto.websocket.outbound.JoinResponse;
import net.prostars.messagesystem.service.ChannelService;
import net.prostars.messagesystem.session.WebSocketSessionManager;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
@SuppressWarnings("unused")
public class JoinRequestHandler implements BaseRequestHandler<JoinRequest> {

  private final ChannelService channelService;
  private final WebSocketSessionManager webSocketSessionManager;

  public JoinRequestHandler(
      ChannelService channelService, WebSocketSessionManager webSocketSessionManager) {
    this.channelService = channelService;
    this.webSocketSessionManager = webSocketSessionManager;
  }

  @Override
  public void handleRequest(WebSocketSession senderSession, JoinRequest request) {
    UserId senderUserId = (UserId) senderSession.getAttributes().get(IdKey.USER_ID.getValue());

    Pair<Optional<Channel>, ResultType> result;
    try {
      result = channelService.join(request.getInviteCode(), senderUserId);
    } catch (Exception ex) {
      webSocketSessionManager.sendMessage(
          senderSession,
          new ErrorResponse(MessageType.JOIN_REQUEST, ResultType.FAILED.getMessage()));
      return;
    }

    result
        .getFirst()
        .ifPresentOrElse(
            channel ->
                webSocketSessionManager.sendMessage(
                    senderSession, new JoinResponse(channel.channelId(), channel.title())),
            () ->
                webSocketSessionManager.sendMessage(
                    senderSession,
                    new ErrorResponse(MessageType.JOIN_REQUEST, result.getSecond().getMessage())));
  }
}
