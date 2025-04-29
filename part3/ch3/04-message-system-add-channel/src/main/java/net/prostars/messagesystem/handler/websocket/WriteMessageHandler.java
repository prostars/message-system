package net.prostars.messagesystem.handler.websocket;

import net.prostars.messagesystem.constant.IdKey;
import net.prostars.messagesystem.dto.domain.ChannelId;
import net.prostars.messagesystem.dto.domain.UserId;
import net.prostars.messagesystem.dto.websocket.inbound.WriteMessage;
import net.prostars.messagesystem.dto.websocket.outbound.MessageNotification;
import net.prostars.messagesystem.service.MessageService;
import net.prostars.messagesystem.service.UserService;
import net.prostars.messagesystem.session.WebSocketSessionManager;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
@SuppressWarnings("unused")
public class WriteMessageHandler implements BaseRequestHandler<WriteMessage> {

  private final UserService userService;
  private final MessageService messageService;
  private final WebSocketSessionManager webSocketSessionManager;

  public WriteMessageHandler(
      UserService userService,
      MessageService messageService,
      WebSocketSessionManager webSocketSessionManager) {
    this.userService = userService;
    this.messageService = messageService;
    this.webSocketSessionManager = webSocketSessionManager;
  }

  @Override
  public void handleRequest(WebSocketSession senderSession, WriteMessage request) {
    UserId senderUserId = (UserId) senderSession.getAttributes().get(IdKey.USER_ID.getValue());
    ChannelId channelId = request.getChannelId();
    String content = request.getContent();
    String senderUsername = userService.getUsername(senderUserId).orElse("unknown");
    messageService.sendMessage(
        senderUserId,
        content,
        channelId,
        (participantId) -> {
          WebSocketSession participantSession = webSocketSessionManager.getSession(participantId);
          MessageNotification messageNotification =
              new MessageNotification(channelId, senderUsername, content);
          if (participantSession != null) {
            webSocketSessionManager.sendMessage(participantSession, messageNotification);
          }
        });
  }
}
