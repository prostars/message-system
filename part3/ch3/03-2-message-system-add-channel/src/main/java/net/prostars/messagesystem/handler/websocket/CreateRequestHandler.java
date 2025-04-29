package net.prostars.messagesystem.handler.websocket;

import java.util.Optional;
import net.prostars.messagesystem.constant.IdKey;
import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.constant.ResultType;
import net.prostars.messagesystem.dto.domain.Channel;
import net.prostars.messagesystem.dto.domain.UserId;
import net.prostars.messagesystem.dto.websocket.inbound.CreateRequest;
import net.prostars.messagesystem.dto.websocket.outbound.CreateResponse;
import net.prostars.messagesystem.dto.websocket.outbound.ErrorResponse;
import net.prostars.messagesystem.dto.websocket.outbound.JoinNotification;
import net.prostars.messagesystem.service.ChannelService;
import net.prostars.messagesystem.service.UserService;
import net.prostars.messagesystem.session.WebSocketSessionManager;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
@SuppressWarnings("unused")
public class CreateRequestHandler implements BaseRequestHandler<CreateRequest> {

  private final ChannelService channelService;
  private final UserService userService;
  private final WebSocketSessionManager webSocketSessionManager;

  public CreateRequestHandler(
      ChannelService channelService,
      UserService userService,
      WebSocketSessionManager webSocketSessionManager) {
    this.channelService = channelService;
    this.userService = userService;
    this.webSocketSessionManager = webSocketSessionManager;
  }

  @Override
  public void handleRequest(WebSocketSession senderSession, CreateRequest request) {
    UserId senderUserId = (UserId) senderSession.getAttributes().get(IdKey.USER_ID.getValue());

    Optional<UserId> userId = userService.getUserId(request.getParticipantUsername());
    if (userId.isEmpty()) {
      webSocketSessionManager.sendMessage(
          senderSession,
          new ErrorResponse(MessageType.CREATE_REQUEST, ResultType.NOT_FOUND.getMessage()));
      return;
    }

    UserId participantId = userId.get();
    Pair<Optional<Channel>, ResultType> result;

    try {
      result = channelService.create(senderUserId, participantId, request.getTitle());
    } catch (Exception ex) {
      webSocketSessionManager.sendMessage(
          senderSession,
          new ErrorResponse(MessageType.CREATE_REQUEST, ResultType.FAILED.getMessage()));
      return;
    }

    result
        .getFirst()
        .ifPresentOrElse(
            channel -> {
              webSocketSessionManager.sendMessage(
                  senderSession, new CreateResponse(channel.channelId(), channel.title()));
              webSocketSessionManager.sendMessage(
                  webSocketSessionManager.getSession(participantId),
                  new JoinNotification(channel.channelId(), channel.title()));
            },
            () ->
                webSocketSessionManager.sendMessage(
                    senderSession,
                    new ErrorResponse(
                        MessageType.CREATE_REQUEST, result.getSecond().getMessage())));
  }
}
