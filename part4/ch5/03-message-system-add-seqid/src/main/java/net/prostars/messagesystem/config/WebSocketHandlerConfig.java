package net.prostars.messagesystem.config;

import net.prostars.messagesystem.auth.WebSocketHttpSessionHandshakeInterceptor;
import net.prostars.messagesystem.handler.WebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@SuppressWarnings("unused")
public class WebSocketHandlerConfig implements WebSocketConfigurer {

  private final WebSocketHandler webSocketHandler;
  private final WebSocketHttpSessionHandshakeInterceptor webSocketHttpSessionHandshakeInterceptor;

  public WebSocketHandlerConfig(
      WebSocketHandler webSocketHandler,
      WebSocketHttpSessionHandshakeInterceptor webSocketHttpSessionHandshakeInterceptor) {
    this.webSocketHandler = webSocketHandler;
    this.webSocketHttpSessionHandshakeInterceptor = webSocketHttpSessionHandshakeInterceptor;
  }

  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    registry
        .addHandler(webSocketHandler, "/ws/v1/message")
        .addInterceptors(webSocketHttpSessionHandshakeInterceptor);
  }
}
