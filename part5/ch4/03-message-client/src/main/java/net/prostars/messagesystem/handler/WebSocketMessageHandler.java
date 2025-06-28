package net.prostars.messagesystem.handler;

import jakarta.websocket.MessageHandler;

public class WebSocketMessageHandler implements MessageHandler.Whole<String> {

  private final InboundMessageHandler inboundMessageHandler;

  public WebSocketMessageHandler(InboundMessageHandler inboundMessageHandler) {
    this.inboundMessageHandler = inboundMessageHandler;
  }

  @Override
  public void onMessage(String payload) {
    inboundMessageHandler.handle(payload);
  }
}
