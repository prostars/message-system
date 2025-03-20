package net.prostars.messagesystem.service;

import jakarta.websocket.CloseReason;
import jakarta.websocket.Session;
import java.io.IOException;
import java.net.URI;
import net.prostars.messagesystem.dto.Message;
import net.prostars.messagesystem.handler.WebSocketMessageHandler;
import net.prostars.messagesystem.handler.WebSocketSender;
import net.prostars.messagesystem.handler.WebSocketSessionHandler;
import org.glassfish.tyrus.client.ClientManager;

public class WebSocketService {

  private final TerminalService terminalService;
  private final WebSocketSender messageSender;
  private final String webSocketUrl;
  private WebSocketMessageHandler webSocketMessageHandler;
  private Session session;

  public WebSocketService(
      TerminalService terminalService, WebSocketSender messageSender, String url, String endpoint) {
    this.terminalService = terminalService;
    this.messageSender = messageSender;
    this.webSocketUrl = "ws://" + url + endpoint;
  }

  public void setWebSocketMessageHandler(WebSocketMessageHandler webSocketMessageHandler) {
    this.webSocketMessageHandler = webSocketMessageHandler;
  }

  public boolean createSession() {
    ClientManager client = ClientManager.createClient();

    try {
      session =
          client.connectToServer(
              new WebSocketSessionHandler(terminalService), new URI(webSocketUrl));
      session.addMessageHandler(webSocketMessageHandler);
      return true;
    } catch (Exception ex) {
      terminalService.printSystemMessage(
          String.format("Failed to connect to [%s] error: %s", webSocketUrl, ex.getMessage()));
      return false;
    }
  }

  public void closeSession() {
    try {
      if (session != null) {
        if (session.isOpen()) {
          session.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "NORMAL CLOSURE"));
        }
        session = null;
      }
    } catch (IOException ex) {
      terminalService.printSystemMessage(
          String.format("Failed to close. error: %s", ex.getMessage()));
    }
  }

  public void sendMessage(Message message) {
    if (session != null && session.isOpen()) {
      messageSender.sendMessage(session, message);
    } else {
      terminalService.printSystemMessage("Failed to send message. Session is not open.");
    }
  }
}
