package net.prostars.messagesystem.service;

import jakarta.websocket.Session;
import java.util.Map;
import java.util.concurrent.*;
import net.prostars.messagesystem.dto.domain.Message;
import net.prostars.messagesystem.dto.domain.MessageSeqId;
import net.prostars.messagesystem.dto.websocket.inbound.FetchMessagesResponse;
import net.prostars.messagesystem.dto.websocket.inbound.MessageNotification;
import net.prostars.messagesystem.dto.websocket.inbound.WriteMessageAck;
import net.prostars.messagesystem.dto.websocket.outbound.FetchMessagesRequest;
import net.prostars.messagesystem.dto.websocket.outbound.ReadMessageAck;
import net.prostars.messagesystem.dto.websocket.outbound.WriteMessage;
import net.prostars.messagesystem.json.JsonUtil;

public class MessageService {

  private final int LIMIT_RETRIES = 3;
  private final long TIMEOUT_MS = 3000L;

  private final UserService userService;
  private final TerminalService terminalService;
  private final Map<Long, CompletableFuture<WriteMessageAck>> pendingMessages =
      new ConcurrentHashMap<>();
  private final Map<MessageSeqIdRange, ScheduledFuture<?>> scheduledFetchMessagesRequests =
      new ConcurrentHashMap<>();
  private final ScheduledExecutorService scheduledExecutorService =
      Executors.newSingleThreadScheduledExecutor();
  private WebSocketService webSocketService;

  public MessageService(UserService userService, TerminalService terminalService) {
    this.userService = userService;
    this.terminalService = terminalService;
  }

  public void setWebSocketService(WebSocketService webSocketService) {
    this.webSocketService = webSocketService;
  }

  public void receiveMessage(WriteMessageAck writeMessageAck) {
    CompletableFuture<WriteMessageAck> future = pendingMessages.get(writeMessageAck.getSerial());
    if (future != null) {
      future.complete(writeMessageAck);
    }
  }

  public void receiveMessage(MessageNotification messageNotification) {
    if (userService.isInLobby()
        || !userService.getChannelId().equals(messageNotification.getChannelId())) {
      terminalService.printSystemMessage("Invalid channelId. Ignore message.");
      return;
    }

    MessageSeqId lastReadMessageSeqId = userService.getLastReadMessageSeqId();
    MessageSeqId receivedmessageSeqId = messageNotification.getMessageSeqId();
    if (lastReadMessageSeqId == null
        || receivedmessageSeqId.id() == lastReadMessageSeqId.id() + 1) {
      for (MessageSeqIdRange idRange : scheduledFetchMessagesRequests.keySet()) {
        if (receivedmessageSeqId.id() >= idRange.start.id()
            && receivedmessageSeqId.id() <= idRange.end.id()) {
          scheduledFetchMessagesRequests.get(idRange).cancel(false);
          scheduledFetchMessagesRequests.remove(idRange);
          if (!idRange.start.equals(idRange.end)) {
            reserveFetchMessagesRequest(idRange.start, idRange.end);
          }
        }
      }

      userService.addMessage(
          new Message(
              messageNotification.getChannelId(),
              messageNotification.getMessageSeqId(),
              messageNotification.getUsername(),
              messageNotification.getContent()));
      processMessageBuffer();
    } else if (receivedmessageSeqId.id() > lastReadMessageSeqId.id() + 1) {
      userService.addMessage(
          new Message(
              messageNotification.getChannelId(),
              messageNotification.getMessageSeqId(),
              messageNotification.getUsername(),
              messageNotification.getContent()));
      reserveFetchMessagesRequest(lastReadMessageSeqId, receivedmessageSeqId);
    } else if (receivedmessageSeqId.id() <= lastReadMessageSeqId.id()) {
      terminalService.printSystemMessage(
          "Ignore duplication message: " + messageNotification.getMessageSeqId());
    }
  }

  public void receiveMessage(FetchMessagesResponse fetchMessagesResponse) {
    if (userService.isInLobby()
        || !userService.getChannelId().equals(fetchMessagesResponse.getChannelId())) {
      terminalService.printSystemMessage("Invalid channelId. Ignore fetch messages.");
      return;
    }
    fetchMessagesResponse.getMessages().forEach(userService::addMessage);
    processMessageBuffer();
  }

  public void sendMessage(Session session, WriteMessage message) {
    sendMessage(session, message, 0);
  }

  public void sendMessage(FetchMessagesRequest fetchMessagesRequest) {
    webSocketService.sendMessage(fetchMessagesRequest);
  }

  private void processMessageBuffer() {
    while (!userService.isBufferEmpty()) {
      Message peekMessage = userService.peekMessage();
      if (userService.getLastReadMessageSeqId() == null
          || peekMessage.messageSeqId().id() == userService.getLastReadMessageSeqId().id() + 1) {
        Message message = userService.popMessage();
        terminalService.printMessage(message.username(), message.content());
        webSocketService.sendMessage(new ReadMessageAck(userService.getChannelId(), message.messageSeqId()));
        userService.setLastReadMessageSeqId(message.messageSeqId());
      } else if (peekMessage.messageSeqId().id() <= userService.getLastReadMessageSeqId().id()) {
        userService.popMessage();
      } else if (peekMessage.messageSeqId().id() > userService.getLastReadMessageSeqId().id() + 1) {
        break;
      }
    }
  }

  private void reserveFetchMessagesRequest(MessageSeqId lastReadSeqId, MessageSeqId receivedSeqid) {
    MessageSeqIdRange messageSeqIdRange =
        new MessageSeqIdRange(
            new MessageSeqId(lastReadSeqId.id() + 1), new MessageSeqId(receivedSeqid.id() - 1));
    ScheduledFuture<?> scheduledFuture =
        scheduledExecutorService.schedule(
            () -> {
              webSocketService.sendMessage(
                  new FetchMessagesRequest(
                      userService.getChannelId(), messageSeqIdRange.start, messageSeqIdRange.end));
              scheduledFetchMessagesRequests.remove(messageSeqIdRange);
            },
            100,
            TimeUnit.MILLISECONDS);
    scheduledFetchMessagesRequests.put(messageSeqIdRange, scheduledFuture);
  }

  private void sendMessage(Session session, WriteMessage message, int retryCount) {
    if (session != null && session.isOpen()) {
      CompletableFuture<WriteMessageAck> future = new CompletableFuture<>();
      future
          .orTimeout(TIMEOUT_MS, TimeUnit.MILLISECONDS)
          .whenCompleteAsync(
              (response, throwable) -> {
                if (response != null) {
                  userService.setLastReadMessageSeqId(response.getMessageSeqId());
                  terminalService.printMessage("<me>", message.getContent());
                  pendingMessages.remove(message.getSerial());
                } else if (throwable instanceof TimeoutException && retryCount < LIMIT_RETRIES) {
                  sendMessage(session, message, retryCount + 1);
                } else {
                  terminalService.printSystemMessage("Send message failed.");
                  pendingMessages.remove(message.getSerial());
                }
              });
      pendingMessages.put(message.getSerial(), future);

      JsonUtil.toJson(message)
          .ifPresent(
              payload ->
                  session
                      .getAsyncRemote()
                      .sendText(
                          payload,
                          result -> {
                            if (!result.isOK()) {
                              terminalService.printSystemMessage(
                                  "'%s' send failed. cause: %s"
                                      .formatted(payload, result.getException()));
                            }
                          }));
    }
  }

  private record MessageSeqIdRange(MessageSeqId start, MessageSeqId end) {}
}
