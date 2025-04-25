package net.prostars.messagesystem.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import net.prostars.messagesystem.dto.restapi.LoginRequest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.util.matcher.RequestMatcher;

public class RestApiLoginAuthFilter extends AbstractAuthenticationProcessingFilter {

  private final ObjectMapper objectMapper = new ObjectMapper();

  public RestApiLoginAuthFilter(
      RequestMatcher requiresAuthenticationRequestMatcher,
      AuthenticationManager authenticationManager) {
    super(requiresAuthenticationRequestMatcher, authenticationManager);
  }

  @Override
  public Authentication attemptAuthentication(
      HttpServletRequest request, HttpServletResponse response)
      throws AuthenticationException, IOException {
    if (!request.getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE)) {
      throw new AuthenticationServiceException(
          "Unsupported Content-Type: " + request.getContentType());
    }

    LoginRequest loginRequest =
        objectMapper.readValue(request.getInputStream(), LoginRequest.class);
    UsernamePasswordAuthenticationToken authenticationToken =
        new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password());
    return getAuthenticationManager().authenticate(authenticationToken);
  }

  @Override
  protected void successfulAuthentication(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain chain,
      Authentication authResult)
      throws IOException {
    SecurityContext securityContext = SecurityContextHolder.getContext();
    ((MessageUserDetails) authResult.getPrincipal()).erasePassword();
    securityContext.setAuthentication(authResult);
    HttpSessionSecurityContextRepository contextRepository =
        new HttpSessionSecurityContextRepository();
    contextRepository.saveContext(securityContext, request, response);

    String sessionId = request.getSession().getId();
    String encodedSessionId =
        Base64.getEncoder().encodeToString(sessionId.getBytes(StandardCharsets.UTF_8));

    response.setStatus(HttpServletResponse.SC_OK);
    response.setContentType(MediaType.TEXT_PLAIN_VALUE);
    response.setCharacterEncoding("UTF-8");
    response.getWriter().write(encodedSessionId);
    response.getWriter().flush();
  }

  @Override
  protected void unsuccessfulAuthentication(
      HttpServletRequest request, HttpServletResponse response, AuthenticationException failed)
      throws IOException {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setCharacterEncoding("UTF-8");
    response.setContentType(MediaType.TEXT_PLAIN_VALUE);
    response.getWriter().write("Not authenticated.");
    response.getWriter().flush();
  }
}
