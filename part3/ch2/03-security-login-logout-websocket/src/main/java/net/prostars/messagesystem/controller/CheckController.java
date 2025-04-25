package net.prostars.messagesystem.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class CheckController {

  @GetMapping("/check")
  public ResponseEntity<String> check() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null && authentication.isAuthenticated()) {
      return ResponseEntity.ok("Authenticated as : " + authentication.getName());
    }
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated.");
  }
}
