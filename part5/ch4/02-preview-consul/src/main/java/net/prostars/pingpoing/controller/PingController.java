package net.prostars.pingpoing.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class PingController {

  private static final Logger log = LoggerFactory.getLogger(PingController.class);

  @Operation(summary = "1을 더해주는 핑", description = "정수를 받아 1을 증가시켜 리턴합니다.")
  @GetMapping("/ping/{count}")
  public String ping(
      @Parameter(description = "정수형 기준 값", example = "123") @PathVariable int count,
      @RequestHeader(value = "Host", required = false) String host,
      @RequestHeader(value = "X-Real-IP", required = false) String realIp,
      @RequestHeader(value = "X-Forwarded-For", required = false) String forwardedFor) {
    log.info("Host: {}, X-Real-IP: {}, X-Forwarded-For: {}", host, realIp, forwardedFor);
    return String.format("pong : %d", count + 1);
  }
}
