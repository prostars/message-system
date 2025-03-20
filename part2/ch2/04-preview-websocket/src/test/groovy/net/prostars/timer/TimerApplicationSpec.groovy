package net.prostars.timer


import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest
class TimerApplicationSpec extends Specification {

    def contextLoads() {
        expect:
        true
    }
}
