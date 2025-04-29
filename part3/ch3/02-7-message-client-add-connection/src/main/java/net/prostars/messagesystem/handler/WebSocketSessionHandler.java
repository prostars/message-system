package net.prostars.messagesystem.handler;

import jakarta.websocket.CloseReason;
import jakarta.websocket.Endpoint;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.Session;
import net.prostars.messagesystem.service.TerminalService;
import net.prostars.messagesystem.service.WebSocketService;

public class WebSocketSessionHandler extends Endpoint {

  private final WebSocketService webSocketService;
  private final TerminalService terminalService;

  public WebSocketSessionHandler(WebSocketService webSocketService, TerminalService terminalService) {
    this.webSocketService = webSocketService;
    this.terminalService = terminalService;
  }

  @Override
  public void onOpen(Session session, EndpointConfig endpointConfig) {
    terminalService.printSystemMessage("WebSocket Connected.");
  }

  @Override
  public void onError(Session session, Throwable thr) {
    terminalService.printSystemMessage("Error: " + thr.getMessage());
  }

  @Override
  public void onClose(Session session, CloseReason closeReason) {
    webSocketService.closeSession();
    terminalService.printSystemMessage("Connection closed: " + closeReason.getReasonPhrase());
  }
}
