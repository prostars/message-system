package net.prostars.messagesystem.handler

import com.fasterxml.jackson.databind.ObjectMapper
import net.prostars.messagesystem.MessageSystemApplication
import net.prostars.messagesystem.dto.Message
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


@SpringBootTest(
        classes = MessageSystemApplication,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MessageHandlerSpec extends Specification {

    @LocalServerPort
    int port

    ObjectMapper objectMapper = new ObjectMapper()

    def "Direct Chat Basic Test"() {
        given:
        def url = "ws://localhost:${port}/ws/v1/message"
        BlockingQueue<String> leftQueue = new ArrayBlockingQueue<>(1)
        BlockingQueue<String> rightQueue = new ArrayBlockingQueue<>(1)

        def leftClient = new StandardWebSocketClient()
        def leftWebSocketSession = leftClient.execute(new TextWebSocketHandler() {
            @Override
            protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
                leftQueue.put(message.payload)
            }
        }, url).get()

        def rightClient = new StandardWebSocketClient()
        def rightWebSocketSession = rightClient.execute(new TextWebSocketHandler() {
            @Override
            protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
                rightQueue.put(message.payload)
            }
        }, url).get()

        when:
        leftWebSocketSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(new Message("안녕하세요."))))
        rightWebSocketSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(new Message("Hello."))))

        then:
        rightQueue.poll(1, TimeUnit.SECONDS).contains("안녕하세요.")

        and:
        leftQueue.poll(1, TimeUnit.SECONDS).contains("Hello.")

        cleanup:
        leftWebSocketSession?.close()
        rightWebSocketSession?.close()
    }
}
