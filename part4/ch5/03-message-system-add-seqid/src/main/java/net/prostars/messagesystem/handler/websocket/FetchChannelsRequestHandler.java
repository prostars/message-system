package net.prostars.messagesystem.handler.websocket;

import net.prostars.messagesystem.constant.IdKey;
import net.prostars.messagesystem.dto.domain.UserId;
import net.prostars.messagesystem.dto.websocket.inbound.FetchChannelsRequest;
import net.prostars.messagesystem.dto.websocket.outbound.FetchChannelsResponse;
import net.prostars.messagesystem.service.ChannelService;
import net.prostars.messagesystem.service.ClientNotificationService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
@SuppressWarnings("unused")
public class FetchChannelsRequestHandler implements BaseRequestHandler<FetchChannelsRequest> {

  private final ChannelService channelService;
  private final ClientNotificationService clientNotificationService;

  public FetchChannelsRequestHandler(
      ChannelService channelService, ClientNotificationService clientNotificationService) {
    this.channelService = channelService;
    this.clientNotificationService = clientNotificationService;
  }

  @Override
  public void handleRequest(WebSocketSession senderSession, FetchChannelsRequest request) {
    UserId senderUserId = (UserId) senderSession.getAttributes().get(IdKey.USER_ID.getValue());

    clientNotificationService.sendMessage(
        senderSession,
        senderUserId,
        new FetchChannelsResponse(channelService.getChannels(senderUserId)));
  }
}
