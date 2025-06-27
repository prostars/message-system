package net.prostars.messagesystem.handler.websocket;

import java.util.List;
import net.prostars.messagesystem.constant.IdKey;
import net.prostars.messagesystem.constant.MessageType;
import net.prostars.messagesystem.constant.ResultType;
import net.prostars.messagesystem.dto.domain.ChannelId;
import net.prostars.messagesystem.dto.domain.Message;
import net.prostars.messagesystem.dto.domain.UserId;
import net.prostars.messagesystem.dto.websocket.inbound.FetchMessagesRequest;
import net.prostars.messagesystem.dto.websocket.outbound.ErrorResponse;
import net.prostars.messagesystem.dto.websocket.outbound.FetchMessagesResponse;
import net.prostars.messagesystem.service.ClientNotificationService;
import net.prostars.messagesystem.service.MessageService;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
@SuppressWarnings("unused")
public class FetchMessagesRequestHandler implements BaseRequestHandler<FetchMessagesRequest> {

  private final MessageService messageService;
  private final ClientNotificationService clientNotificationService;

  public FetchMessagesRequestHandler(
      MessageService messageService, ClientNotificationService clientNotificationService) {
    this.messageService = messageService;
    this.clientNotificationService = clientNotificationService;
  }

  @Override
  public void handleRequest(WebSocketSession senderSession, FetchMessagesRequest request) {
    UserId senderUserId = (UserId) senderSession.getAttributes().get(IdKey.USER_ID.getValue());
    ChannelId channelId = request.getChannelId();
    Pair<List<Message>, ResultType> result =
        messageService.getMessages(
            channelId, request.getStartMessageSeqId(), request.getEndMessageSeqId());
    if (result.getSecond() == ResultType.SUCCESS) {
      List<Message> messages = result.getFirst();
      clientNotificationService.sendMessage(
          senderSession, senderUserId, new FetchMessagesResponse(channelId, messages));
    } else {
      clientNotificationService.sendMessage(
          senderSession,
          senderUserId,
          new ErrorResponse(MessageType.FETCH_MESSAGES_REQUEST, result.getSecond().getMessage()));
    }
  }
}
