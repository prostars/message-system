package net.prostars.messagesystem.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.prostars.messagesystem.constant.KeyPrefix;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.session.FlushMode;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@Configuration
@EnableRedisHttpSession(
    redisNamespace = KeyPrefix.USER_SESSION,
    maxInactiveIntervalInSeconds = 300,
    flushMode = FlushMode.IMMEDIATE)
@SuppressWarnings("unused")
public class RedisSessionConfig {

  @Bean
  public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModules(SecurityJackson2Modules.getModules(getClass().getClassLoader()));
    return new GenericJackson2JsonRedisSerializer(mapper);
  }
}
