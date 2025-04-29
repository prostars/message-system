package net.prostars.messagesystem.auth;

import net.prostars.messagesystem.entity.UserEntity;
import net.prostars.messagesystem.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@SuppressWarnings("unused")
public class MessageUserDetailsService implements UserDetailsService {

  private static final Logger log = LoggerFactory.getLogger(MessageUserDetailsService.class);
  private final UserRepository userRepository;

  public MessageUserDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    UserEntity userEntity =
        userRepository
            .findByUsername(username)
            .orElseThrow(
                () -> {
                  log.info("User not found: {}", username);
                  return new UsernameNotFoundException("");
                });

    return new MessageUserDetails(
        userEntity.getUserId(), userEntity.getUsername(), userEntity.getPassword());
  }
}
