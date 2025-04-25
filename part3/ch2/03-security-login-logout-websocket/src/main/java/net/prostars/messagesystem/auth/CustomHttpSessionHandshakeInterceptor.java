package net.prostars.messagesystem.auth;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import net.prostars.messagesystem.session.HttpSessionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

@Component
public class CustomHttpSessionHandshakeInterceptor extends HttpSessionHandshakeInterceptor {

  private static final Logger log =
      LoggerFactory.getLogger(CustomHttpSessionHandshakeInterceptor.class);
  private final HttpSessionRepository httpSessionRepository;

  public CustomHttpSessionHandshakeInterceptor(HttpSessionRepository httpSessionRepository) {
    this.httpSessionRepository = httpSessionRepository;
  }

  @Override
  public boolean beforeHandshake(
      ServerHttpRequest request,
      @NonNull ServerHttpResponse response,
      @NonNull WebSocketHandler wsHandler,
      @NonNull Map<String, Object> attributes) {
    List<String> cookies = request.getHeaders().get("Cookie");
    if (cookies != null) {
      for (String cookie : cookies) {
        if (cookie.contains("JSESSIONID")) {
          String sessionId = cookie.split("=")[1];
          HttpSession httpSession = httpSessionRepository.findById(sessionId);
          if (httpSession != null) {
            log.info("Connected sessionId : {}", sessionId);
            return true;
          }
        }
      }
    }

    log.info("Unauthorized access attempt : ClientIP={}", request.getRemoteAddress());
    response.setStatusCode(HttpStatus.UNAUTHORIZED);
    return false;
  }
}
