package net.prostars.pingpoing

import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification;

@SpringBootTest
class PingPongApplicationSpec extends Specification{

	void contextLoads() {
		expect:
		true
	}
}
