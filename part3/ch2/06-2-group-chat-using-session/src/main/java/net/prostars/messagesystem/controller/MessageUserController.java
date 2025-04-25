package net.prostars.messagesystem.controller;

import jakarta.servlet.http.HttpServletRequest;
import net.prostars.messagesystem.dto.restapi.UserRegisterRequest;
import net.prostars.messagesystem.service.MessageUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@SuppressWarnings("unused")
public class MessageUserController {

  private static final Logger log = LoggerFactory.getLogger(MessageUserController.class);
  private final MessageUserService messageUserService;

  public MessageUserController(MessageUserService messageUserService) {
    this.messageUserService = messageUserService;
  }

  @PostMapping("/register")
  public ResponseEntity<String> register(@RequestBody UserRegisterRequest request) {
    try {
      messageUserService.addUser(request.username(), request.password());
      return ResponseEntity.ok("User registered.");
    } catch (Exception ex) {
      log.error("Failed to add user. cause: {}", ex.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to register.");
    }
  }

  @PostMapping("/unregister")
  public ResponseEntity<String> unregister(HttpServletRequest request) {
    try {
      messageUserService.removeUser();
      request.getSession().invalidate();
      return ResponseEntity.ok("User unregistered.");
    } catch (Exception ex) {
      log.error("Failed to remove user. cause: {}", ex.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to unregister user.");
    }
  }
}
