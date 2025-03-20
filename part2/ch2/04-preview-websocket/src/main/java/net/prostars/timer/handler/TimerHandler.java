package net.prostars.timer.handler;

import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class TimerHandler extends TextWebSocketHandler {

  private static final Logger log = LoggerFactory.getLogger(TimerHandler.class);
  private final ScheduledExecutorService scheduledExecutorService =
      Executors.newSingleThreadScheduledExecutor();

  @Override
  public void afterConnectionEstablished(WebSocketSession session) {
    log.info("Connection Established: {}", session.getId());
  }

  @Override
  protected void handleTextMessage(WebSocketSession session, @NonNull TextMessage message) {
    log.info("Received TextMessage: [{}] from {}", message, session.getId());

    try {
      int seconds = Integer.parseInt(message.getPayload());
      long timestamp = Instant.now().toEpochMilli();
      scheduledExecutorService.schedule(
          () -> sendMessage(session, String.format("%d에 등록한 %d초 타이머 완료.", timestamp, seconds)),
          seconds,
          TimeUnit.SECONDS);
      String responseMessage = String.format("%d에 %d초 타이머 등록 완료.", timestamp, seconds);
      sendMessage(session, responseMessage);
    } catch (NumberFormatException ignored) {
      sendMessage(session, "정수가 아님. 타이머 등록 실패.");
    }
  }

  private void sendMessage(WebSocketSession session, String message) {
    try {
      session.sendMessage(new TextMessage(message));
      log.info("send message: {} to {}", message, session.getId());
    } catch (IOException ex) {
      log.error("메시지 전송 실패. error: {}", ex.getMessage());
    }
  }

  @Override
  public void handleTransportError(WebSocketSession session, Throwable exception) {
    log.error("TransportError: [{}] from {}", exception.getMessage(), session.getId());
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, @NonNull CloseStatus status) {
    log.info("Connection Closed: [{}] from {}", status, session.getId());
  }
}
