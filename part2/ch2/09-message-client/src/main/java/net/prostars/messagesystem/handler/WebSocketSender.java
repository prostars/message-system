package net.prostars.messagesystem.handler;

import jakarta.websocket.Session;
import java.io.IOException;
import net.prostars.messagesystem.dto.Message;
import net.prostars.messagesystem.json.JsonUtil;
import net.prostars.messagesystem.service.TerminalService;

public class WebSocketSender {

  private final TerminalService terminalService;

  public WebSocketSender(TerminalService terminalService) {
    this.terminalService = terminalService;
  }

  public void sendMessage(Session session, Message message) {
    if (session != null && session.isOpen()) {
      JsonUtil.toJson(message)
          .ifPresent(
              msg -> {
                try {
                  session.getBasicRemote().sendText(msg);
                } catch (IOException ex) {
                  terminalService.printSystemMessage(
                      String.format("%s send failed: %s", message, ex.getMessage()));
                }
              });
    }
  }
}
