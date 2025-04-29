package net.prostars.messagesystem.handler.websocket;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
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

    List<UserId> participantIds = userService.getUserIds(request.getParticipantUsernames());
    if (participantIds.isEmpty()) {
      webSocketSessionManager.sendMessage(
          senderSession,
          new ErrorResponse(MessageType.CREATE_REQUEST, ResultType.NOT_FOUND.getMessage()));
      return;
    }

    Pair<Optional<Channel>, ResultType> result;
    try {
      result = channelService.create(senderUserId, participantIds, request.getTitle());
    } catch (Exception ex) {
      webSocketSessionManager.sendMessage(
          senderSession,
          new ErrorResponse(MessageType.CREATE_REQUEST, ResultType.FAILED.getMessage()));
      return;
    }

    if (result.getFirst().isEmpty()) {
      webSocketSessionManager.sendMessage(
          senderSession,
          new ErrorResponse(MessageType.CREATE_REQUEST, result.getSecond().getMessage()));
      return;
    }

    Channel channel = result.getFirst().get();
    webSocketSessionManager.sendMessage(
        senderSession, new CreateResponse(channel.channelId(), channel.title()));
    participantIds.forEach(
        participantId ->
            CompletableFuture.runAsync(
                () -> {
                  WebSocketSession participantSession =
                      webSocketSessionManager.getSession(participantId);
                  if (participantSession != null) {
                    webSocketSessionManager.sendMessage(
                        participantSession,
                        new JoinNotification(channel.channelId(), channel.title()));
                  }
                }));
  }
}
