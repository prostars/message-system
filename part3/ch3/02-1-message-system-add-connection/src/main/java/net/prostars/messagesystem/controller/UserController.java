package net.prostars.messagesystem.controller;

import jakarta.servlet.http.HttpServletRequest;
import net.prostars.messagesystem.dto.restapi.UserRegisterRequest;
import net.prostars.messagesystem.service.UserService;
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
public class UserController {

  private static final Logger log = LoggerFactory.getLogger(UserController.class);
  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping("/register")
  public ResponseEntity<String> register(@RequestBody UserRegisterRequest request) {
    try {
      userService.addUser(request.username(), request.password());
      return ResponseEntity.ok("User registered.");
    } catch (Exception ex) {
      log.error("Register user failed. cause: {}", ex.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Register user failed.");
    }
  }

  @PostMapping("/unregister")
  public ResponseEntity<String> unregister(HttpServletRequest request) {
    try {
      userService.removeUser();
      request.getSession().invalidate();
      return ResponseEntity.ok("User unregistered.");
    } catch (Exception ex) {
      log.error("Unregister user failed. cause: {}", ex.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unregister user failed.");
    }
  }
}
