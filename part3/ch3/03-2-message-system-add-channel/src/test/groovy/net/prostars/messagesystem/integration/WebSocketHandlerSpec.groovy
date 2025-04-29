package net.prostars.messagesystem.integration

import com.fasterxml.jackson.databind.ObjectMapper
import net.prostars.messagesystem.MessageSystemApplication
import net.prostars.messagesystem.dto.domain.ChannelId
import net.prostars.messagesystem.dto.domain.UserId
import net.prostars.messagesystem.dto.websocket.inbound.WriteMessage
import net.prostars.messagesystem.service.ChannelService
import net.prostars.messagesystem.service.UserService
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.web.client.RestTemplate
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketHttpHeaders
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
class WebSocketHandlerSpec extends Specification {

    @LocalServerPort
    int port

    @Autowired
    ObjectMapper objectMapper;
    
    @Autowired
    UserService userService;
    
    @SpringBean
    ChannelService channelService = Stub()

    def "Group Chat Basic Test"() {
        given:        
        register("testuserA", "testpassA")
        register("testuserB", "testpassB")
        def sessionIdA = login("testuserA", "testpassA")
        def sessionIdB = login("testuserB", "testpassB")
        def (clientA, clientB) = [createClint(sessionIdA), createClint(sessionIdB)]

        channelService.getParticipantIds(_ as ChannelId) >> List.of(
                userService.getUserId("testuserA").get(),
                userService.getUserId("testuserB").get())
        channelService.isOnline(_ as UserId, _ as ChannelId) >> true

        when:
        clientA.session.sendMessage(new TextMessage(objectMapper.writeValueAsString(new WriteMessage(new ChannelId(1), "안녕하세요. A 입니다."))))
        clientB.session.sendMessage(new TextMessage(objectMapper.writeValueAsString(new WriteMessage(new ChannelId(1), "안녕하세요. B 입니다."))))

        then:
        def resultA = clientA.queue.poll(1, TimeUnit.SECONDS)
        def resultB = clientB.queue.poll(1, TimeUnit.SECONDS)
        resultA.contains("testuserB")
        resultB.contains("testuserA")

        and:
        clientA.queue.isEmpty()
        clientB.queue.isEmpty()

        cleanup:
        unregister(sessionIdA)
        unregister(sessionIdB)
        clientA.session?.close()
        clientB.session?.close()
    }

    def register(String username, String password) {
        def url = "http://localhost:${port}/api/v1/auth/register"
        def headers = new HttpHeaders(["Content-Type": "application/json"])
        def jsonBody = objectMapper.writeValueAsString([username: username, password: password])
        def httpEntity = new HttpEntity(jsonBody, headers)
        try {
            new RestTemplate().exchange(url, HttpMethod.POST, httpEntity, String)
        } catch (Exception ignore) {
        }
    }

    def unregister(String sessionId) {
        def url = "http://localhost:${port}/api/v1/auth/unregister"
        def headers = new HttpHeaders()
        headers.add("Content-Type", "application/json")
        headers.add("Cookie", "SESSION=${sessionId}")
        def httpEntity = new HttpEntity(headers)
        def responseEntity = new RestTemplate().exchange(url, HttpMethod.POST, httpEntity, String)
        responseEntity.body
    }

    def login(String username, String password) {
        def url = "http://localhost:${port}/api/v1/auth/login"
        def headers = new HttpHeaders(["Content-Type": "application/json"])
        def jsonBody = objectMapper.writeValueAsString([username: username, password: password])
        def httpEntity = new HttpEntity(jsonBody, headers)
        def responseEntity = new RestTemplate().exchange(url, HttpMethod.POST, httpEntity, String)
        def sessionId = responseEntity.body
        sessionId
    }

    def createClint(String sessionId) {
        def url = "ws://localhost:${port}/ws/v1/message"
        BlockingQueue<String> blockingQueue = new ArrayBlockingQueue<>(5)
        def webSocketHttpHeaders = new WebSocketHttpHeaders()
        webSocketHttpHeaders.add("Cookie", "SESSION=${sessionId}")
        def client = new StandardWebSocketClient()
        def webSocketSession = client.execute(new TextWebSocketHandler() {
            @Override
            protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
                blockingQueue.put(message.payload)
            }
        }, webSocketHttpHeaders, new URI(url)).get()

        [queue: blockingQueue, session: webSocketSession]
    }
}
