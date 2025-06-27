package net.prostars.messagesystem.handler.websocket;

import net.prostars.messagesystem.constant.IdKey;
import net.prostars.messagesystem.dto.domain.ChannelId;
import net.prostars.messagesystem.dto.domain.UserId;
import net.prostars.messagesystem.dto.websocket.inbound.WriteMessage;
import net.prostars.messagesystem.dto.websocket.outbound.MessageNotification;
import net.prostars.messagesystem.service.MessageSeqIdGenerator;
import net.prostars.messagesystem.service.MessageService;
import net.prostars.messagesystem.service.UserService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
@SuppressWarnings("unused")
public class WriteMessageHandler implements BaseRequestHandler<WriteMessage> {

  private final UserService userService;
  private final MessageService messageService;
  private final MessageSeqIdGenerator messageSeqIdGenerator;

  public WriteMessageHandler(
      UserService userService,
      MessageService messageService,
      MessageSeqIdGenerator messageSeqIdGenerator) {
    this.userService = userService;
    this.messageService = messageService;
    this.messageSeqIdGenerator = messageSeqIdGenerator;
  }

  @Override
  public void handleRequest(WebSocketSession senderSession, WriteMessage request) {
    UserId senderUserId = (UserId) senderSession.getAttributes().get(IdKey.USER_ID.getValue());
    ChannelId channelId = request.getChannelId();
    String content = request.getContent();
    String senderUsername = userService.getUsername(senderUserId).orElse("unknown");
    
    messageSeqIdGenerator.getNext(channelId).ifPresent(messageSeqId ->
            messageService.sendMessage(
                    senderUserId,
                    content,
                    channelId,
                    messageSeqId,
                    request.getSerial(),
                    new MessageNotification(channelId, messageSeqId, senderUsername, content)));
    
  }
}
