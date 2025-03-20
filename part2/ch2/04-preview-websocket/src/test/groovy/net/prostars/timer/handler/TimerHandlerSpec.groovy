package net.prostars.timer.handler

import net.prostars.timer.TimerApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.client.standard.StandardWebSocketClient
import org.springframework.web.socket.handler.TextWebSocketHandler
import spock.lang.Specification

import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue
import java.util.concurrent.TimeUnit

@SpringBootTest(classes = TimerApplication,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TimerHandlerSpec extends Specification {

    @LocalServerPort
    private int port;

    def "timer 동작 테스트"() {
        given:
        def url = "ws://localhost:${port}/ws/timer"
        BlockingQueue<String> queue = new ArrayBlockingQueue<>(1)
        StandardWebSocketClient client = new StandardWebSocketClient()
        WebSocketSession webSocketSession = client.execute(new TextWebSocketHandler() {
            @Override
            protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
                queue.put(message.payload)
            }
        }, url).get()

        when:
        webSocketSession.sendMessage(new TextMessage("2"))

        then:
        queue.poll(1, TimeUnit.SECONDS).contains('등록 완료')

        and:
        queue.poll(5, TimeUnit.SECONDS).contains('타이머 완료')

        cleanup:
        webSocketSession.close()
    }
}
