package net.prostars.messagesystem;

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import spock.lang.Specification;

@SpringBootTest(classes = MessageSystemApplication)
@TestPropertySource(properties = ["server.port=8090", "server.id=test"])
class MessageSystemApplicationSpec extends Specification {

    void contextLoads() {
        expect:
        true
    }
}
