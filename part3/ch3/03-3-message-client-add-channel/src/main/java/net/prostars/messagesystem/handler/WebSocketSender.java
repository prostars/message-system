package net.prostars.messagesystem.handler;

import jakarta.websocket.Session;
import net.prostars.messagesystem.dto.websocket.outbound.WriteMessage;
import net.prostars.messagesystem.json.JsonUtil;
import net.prostars.messagesystem.service.TerminalService;

public class WebSocketSender {

  private final TerminalService terminalService;

  public WebSocketSender(TerminalService terminalService) {
    this.terminalService = terminalService;
  }

  public void sendMessage(Session session, WriteMessage message) {
    if (session != null && session.isOpen()) {
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
}
