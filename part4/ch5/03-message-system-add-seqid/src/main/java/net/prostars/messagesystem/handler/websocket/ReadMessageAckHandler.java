package net.prostars.messagesystem.handler.websocket;

import net.prostars.messagesystem.constant.IdKey;
import net.prostars.messagesystem.dto.domain.UserId;
import net.prostars.messagesystem.dto.websocket.inbound.ReadMessageAck;
import net.prostars.messagesystem.service.MessageService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
@SuppressWarnings("unused")
public class ReadMessageAckHandler implements BaseRequestHandler<ReadMessageAck> {

  private final MessageService messageService;

  public ReadMessageAckHandler(MessageService messageService) {
    this.messageService = messageService;
  }

  @Override
  public void handleRequest(WebSocketSession senderSession, ReadMessageAck request) {
    UserId senderUserId = (UserId) senderSession.getAttributes().get(IdKey.USER_ID.getValue());
    messageService.updateLastReadMsgSeq(
        senderUserId, request.getChannelId(), request.getMessageSeqId());
  }
}
