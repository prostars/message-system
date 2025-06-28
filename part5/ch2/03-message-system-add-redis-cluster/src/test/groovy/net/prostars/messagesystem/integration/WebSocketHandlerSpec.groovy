package net.prostars.messagesystem.integration

import com.fasterxml.jackson.databind.ObjectMapper
import net.prostars.messagesystem.MessageSystemApplication
import net.prostars.messagesystem.database.ShardContext
import net.prostars.messagesystem.dto.domain.ChannelId
import net.prostars.messagesystem.dto.domain.UserId
import net.prostars.messagesystem.dto.websocket.inbound.WriteMessage
import net.prostars.messagesystem.entity.ChannelSequenceId
import net.prostars.messagesystem.repository.MessageRepository
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

    int restPort = 8080
    
    @LocalServerPort
    int port

    @Autowired
    ObjectMapper objectMapper;
    
    @Autowired
    UserService userService;
    
    @Autowired
    MessageRepository messageRepository
    
    @SpringBean
    ChannelService channelService = Stub()

    def "Group Chat Basic Test"() {
        given:        
        register("testuserA", "testpassA")
        register("testuserB", "testpassB")
        register("testuserC", "testpassC")
        def sessionIdA = login("testuserA", "testpassA")
        def sessionIdB = login("testuserB", "testpassB")
        def sessionIdC = login("testuserC", "testpassC")
        def (clientA, clientB, clientC) = [createClint(sessionIdA), createClint(sessionIdB), createClint(sessionIdC)]

        channelService.getParticipantIds(_ as ChannelId) >> List.of(
                userService.getUserId("testuserA").get(),
                userService.getUserId("testuserB").get(),
                userService.getUserId("testuserC").get())
        channelService.getOnlineParticipantIds(_ as ChannelId, _ as List<UserId>) >> List.of(
                userService.getUserId("testuserA").get(),
                userService.getUserId("testuserB").get(),
                userService.getUserId("testuserC").get())

        when:
        clientA.session.sendMessage(new TextMessage(objectMapper.writeValueAsString(new WriteMessage(1, new ChannelId(1), "안녕하세요. A 입니다."))))
        clientB.session.sendMessage(new TextMessage(objectMapper.writeValueAsString(new WriteMessage(1, new ChannelId(1), "안녕하세요. B 입니다."))))
        clientC.session.sendMessage(new TextMessage(objectMapper.writeValueAsString(new WriteMessage(1, new ChannelId(1), "안녕하세요. C 입니다."))))

        then:
        def resultA = clientA.queue.poll(1, TimeUnit.SECONDS) + clientA.queue.poll(1, TimeUnit.SECONDS) + clientA.queue.poll(1, TimeUnit.SECONDS)
        def resultB = clientB.queue.poll(1, TimeUnit.SECONDS) + clientB.queue.poll(1, TimeUnit.SECONDS) + clientB.queue.poll(1, TimeUnit.SECONDS)
        def resultC = clientC.queue.poll(1, TimeUnit.SECONDS) + clientC.queue.poll(1, TimeUnit.SECONDS) + clientC.queue.poll(1, TimeUnit.SECONDS)
        resultA.contains("WRITE_MESSAGE_ACK") && resultA.contains("testuserB") && resultA.contains("testuserC") 
        resultB.contains("WRITE_MESSAGE_ACK") && resultB.contains("testuserA") && resultB.contains("testuserC")
        resultC.contains("WRITE_MESSAGE_ACK") && resultC.contains("testuserA") && resultC.contains("testuserB")

        and:
        clientA.queue.isEmpty()
        clientB.queue.isEmpty()
        clientC.queue.isEmpty()

        cleanup:
        deleteMessages([resultA, resultC, resultC])
        unregister(sessionIdA)
        unregister(sessionIdB)
        unregister(sessionIdC)
        clientA.session?.close()
        clientB.session?.close()
        clientC.session?.close()
    }
    
    def deleteMessages(List<String> results) {
        try (ShardContext.ShardContextScope ignored = new ShardContext.ShardContextScope(1)) {
            def seqIds = results.collectMany { text -> (text =~ /"messageSeqId":(\d+)/).collect { it[1] } }
            seqIds.forEach { messageRepository.deleteById(new ChannelSequenceId(1, it as Long)) }
        } 
    }

    def register(String username, String password) {
        def url = "http://localhost:${restPort}/api/v1/auth/register"
        def headers = new HttpHeaders(["Content-Type": "application/json"])
        def jsonBody = objectMapper.writeValueAsString([username: username, password: password])
        def httpEntity = new HttpEntity(jsonBody, headers)
        try {
            new RestTemplate().exchange(url, HttpMethod.POST, httpEntity, String)
        } catch (Exception ignore) {
        }
    }

    def unregister(String sessionId) {
        def url = "http://localhost:${restPort}/api/v1/auth/unregister"
        def headers = new HttpHeaders()
        headers.add("Content-Type", "application/json")
        headers.add("Cookie", "SESSION=${sessionId}")
        def httpEntity = new HttpEntity(headers)
        def responseEntity = new RestTemplate().exchange(url, HttpMethod.POST, httpEntity, String)
        responseEntity.body
    }

    def login(String username, String password) {
        def url = "http://localhost:${restPort}/api/v1/auth/login"
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
