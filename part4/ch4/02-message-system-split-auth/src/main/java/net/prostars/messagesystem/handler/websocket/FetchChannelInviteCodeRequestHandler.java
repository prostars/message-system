package net.prostars.messagesystem.handler.websocket;

import net.prostars.messagesystem.constant.IdKey;
import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.dto.domain.UserId;
import net.prostars.messagesystem.dto.websocket.inbound.FetchChannelInviteCodeRequest;
import net.prostars.messagesystem.dto.websocket.outbound.ErrorResponse;
import net.prostars.messagesystem.dto.websocket.outbound.FetchChannelInviteCodeResponse;
import net.prostars.messagesystem.service.ChannelService;
import net.prostars.messagesystem.service.ClientNotificationService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
@SuppressWarnings("unused")
public class FetchChannelInviteCodeRequestHandler
    implements BaseRequestHandler<FetchChannelInviteCodeRequest> {

  private final ChannelService channelService;
  private final ClientNotificationService clientNotificationService;

  public FetchChannelInviteCodeRequestHandler(
      ChannelService channelService, ClientNotificationService clientNotificationService) {
    this.channelService = channelService;
    this.clientNotificationService = clientNotificationService;
  }

  @Override
  public void handleRequest(WebSocketSession senderSession, FetchChannelInviteCodeRequest request) {
    UserId senderUserId = (UserId) senderSession.getAttributes().get(IdKey.USER_ID.getValue());

    if (!channelService.isJoined(request.getChannelId(), senderUserId)) {
      clientNotificationService.sendMessage(
          senderSession,
          senderUserId,
          new ErrorResponse(
              MessageType.FETCH_CHANNEL_INVITECODE_REQUEST, "Not joined the channel."));
      return;
    }

    channelService
        .getInviteCode(request.getChannelId())
        .ifPresentOrElse(
            inviteCode ->
                clientNotificationService.sendMessage(
                    senderSession,
                    senderUserId,
                    new FetchChannelInviteCodeResponse(request.getChannelId(), inviteCode)),
            () ->
                clientNotificationService.sendMessage(
                    senderSession,
                    senderUserId,
                    new ErrorResponse(
                        MessageType.FETCH_CHANNEL_INVITECODE_REQUEST,
                        "Fetch channel invite code failed.")));
  }
}
