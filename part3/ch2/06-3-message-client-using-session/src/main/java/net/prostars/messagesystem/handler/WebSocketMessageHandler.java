package net.prostars.messagesystem.handler;

import jakarta.websocket.MessageHandler;
import net.prostars.messagesystem.dto.domain.Message;
import net.prostars.messagesystem.json.JsonUtil;
import net.prostars.messagesystem.service.TerminalService;

public class WebSocketMessageHandler implements MessageHandler.Whole<String> {

  private final TerminalService terminalService;

  public WebSocketMessageHandler(TerminalService terminalService) {
    this.terminalService = terminalService;
  }

  @Override
  public void onMessage(String payload) {
    JsonUtil.fromJson(payload, Message.class)
        .ifPresent(message -> terminalService.printMessage(message.username(), message.content()));
  }
}
