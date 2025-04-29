package net.prostars.messagesystem.handler.websocket;

import java.util.Optional;
import net.prostars.messagesystem.constant.IdKey;
import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.constant.ResultType;
import net.prostars.messagesystem.dto.domain.UserId;
import net.prostars.messagesystem.dto.websocket.inbound.EnterRequest;
import net.prostars.messagesystem.dto.websocket.outbound.EnterResponse;
import net.prostars.messagesystem.dto.websocket.outbound.ErrorResponse;
import net.prostars.messagesystem.service.ChannelService;
import net.prostars.messagesystem.service.ClientNotificationService;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
@SuppressWarnings("unused")
public class EnterRequestHandler implements BaseRequestHandler<EnterRequest> {

  private final ChannelService channelService;
  private final ClientNotificationService clientNotificationService;

  public EnterRequestHandler(
      ChannelService channelService, ClientNotificationService clientNotificationService) {
    this.channelService = channelService;
    this.clientNotificationService = clientNotificationService;
  }

  @Override
  public void handleRequest(WebSocketSession senderSession, EnterRequest request) {
    UserId senderUserId = (UserId) senderSession.getAttributes().get(IdKey.USER_ID.getValue());

    Pair<Optional<String>, ResultType> result =
        channelService.enter(request.getChannelId(), senderUserId);
    result
        .getFirst()
        .ifPresentOrElse(
            title ->
                clientNotificationService.sendMessage(
                    senderSession, senderUserId, new EnterResponse(request.getChannelId(), title)),
            () ->
                clientNotificationService.sendMessage(
                    senderSession,
                    senderUserId,
                    new ErrorResponse(MessageType.ENTER_REQUEST, result.getSecond().getMessage())));
  }
}
