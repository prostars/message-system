package net.prostars.pingpoing.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import spock.lang.Specification

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PingControllerSpec extends Specification {
    
    @LocalServerPort
    int port
    
    @Autowired
    TestRestTemplate restTemplate
    
    def "Ping API는 받은 count 값에 1을 더한 값을 리턴한다."() {
        given:
        def count = 1
        def url = "http://localhost:${port}/api/ping/${count}"

        when:
        def responseEntity = restTemplate.getForEntity(url, String)

        then:
        responseEntity.statusCode.value() == 200
        responseEntity.body == "pong : ${count + 1}"
    }
}
