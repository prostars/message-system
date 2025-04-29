package net.prostars.messagesystem.handler.websocket;

import net.prostars.messagesystem.constant.IdKey;
import net.prostars.messagesystem.dto.domain.UserId;
import net.prostars.messagesystem.dto.websocket.inbound.FetchChannelsRequest;
import net.prostars.messagesystem.dto.websocket.outbound.FetchChannelsResponse;
import net.prostars.messagesystem.service.ChannelService;
import net.prostars.messagesystem.session.WebSocketSessionManager;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
@SuppressWarnings("unused")
public class FetchChannelsRequestHandler implements BaseRequestHandler<FetchChannelsRequest> {

  private final ChannelService channelService;
  private final WebSocketSessionManager webSocketSessionManager;

  public FetchChannelsRequestHandler(
      ChannelService channelService, WebSocketSessionManager webSocketSessionManager) {
    this.channelService = channelService;
    this.webSocketSessionManager = webSocketSessionManager;
  }

  @Override
  public void handleRequest(WebSocketSession senderSession, FetchChannelsRequest request) {
    UserId senderUserId = (UserId) senderSession.getAttributes().get(IdKey.USER_ID.getValue());

    webSocketSessionManager.sendMessage(
        senderSession, new FetchChannelsResponse(channelService.getChannels(senderUserId)));
  }
}
