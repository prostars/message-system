package net.prostars.messagesystem.config;

import net.prostars.messagesystem.handler.MessageHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@SuppressWarnings("unused")
public class WebSocketHandlerConfig implements WebSocketConfigurer {

  private final MessageHandler messageHandler;

  public WebSocketHandlerConfig(MessageHandler messageHandler) {
    this.messageHandler = messageHandler;
  }

  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    registry.addHandler(messageHandler, "/ws/v1/message");
  }
}
