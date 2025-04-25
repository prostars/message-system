package net.prostars.messagesystem.config;

import net.prostars.messagesystem.auth.CustomHttpSessionHandshakeInterceptor;
import net.prostars.messagesystem.handler.WebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

  private final WebSocketHandler webSocketHandler;
  private final CustomHttpSessionHandshakeInterceptor customHttpSessionHandshakeInterceptor;

  public WebSocketConfig(
      WebSocketHandler webSocketHandler,
      CustomHttpSessionHandshakeInterceptor customHttpSessionHandshakeInterceptor) {
    this.webSocketHandler = webSocketHandler;
    this.customHttpSessionHandshakeInterceptor = customHttpSessionHandshakeInterceptor;
  }

  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    registry
        .addHandler(webSocketHandler, "/ws/v1/connect");
//        .addInterceptors(customHttpSessionHandshakeInterceptor);
  }
}
