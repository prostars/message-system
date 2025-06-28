package net.prostars.messagesystem;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = "server.port=8090")
class AuthApplicationTests {

  @Test
  void contextLoads() {}
}
