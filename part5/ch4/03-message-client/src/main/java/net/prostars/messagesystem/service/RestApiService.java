package net.prostars.messagesystem.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import net.prostars.messagesystem.dto.ServerEndpoint;
import net.prostars.messagesystem.dto.restapi.LoginRequest;
import net.prostars.messagesystem.dto.restapi.UserRegisterRequest;
import net.prostars.messagesystem.json.JsonUtil;
import org.glassfish.grizzly.http.util.HttpStatus;

public class RestApiService {

  private final TerminalService terminalService;
  private final List<ServerEndpoint> serverEndpoints = new ArrayList<>();
  private String sessionId = "";

  public RestApiService(
      TerminalService terminalService, List<String> serviceDiscoveryUrls, String path) {
    this.terminalService = terminalService;
    prepareServerEndpoints(serviceDiscoveryUrls, path);
  }

  public List<ServerEndpoint> getServerEndpoints() {
    return serverEndpoints;
  }

  public String getSessionId() {
    return sessionId;
  }

  public boolean register(String username, String password) {
    return requestWithRetry(
            "/api/v1/auth/register", "", new UserRegisterRequest(username, password))
        .filter(httpResponse -> httpResponse.statusCode() == HttpStatus.OK_200.getStatusCode())
        .isPresent();
  }

  public boolean unregister() {
    if (sessionId.isEmpty()) {
      return false;
    }
    return requestWithRetry("/api/v1/auth/unregister", sessionId, null)
        .filter(httpResponse -> httpResponse.statusCode() == HttpStatus.OK_200.getStatusCode())
        .isPresent();
  }

  public boolean login(String username, String password) {
    return requestWithRetry("/api/v1/auth/login", "", new LoginRequest(username, password))
        .map(
            httpResponse -> {
              if (httpResponse.statusCode() == HttpStatus.OK_200.getStatusCode()) {
                sessionId = httpResponse.body();
                return true;
              }
              return false;
            })
        .orElse(false);
  }

  public boolean logout() {
    if (sessionId.isEmpty()) {
      return false;
    }
    return requestWithRetry("/api/v1/auth/logout", sessionId, null)
        .filter(httpResponse -> httpResponse.statusCode() == HttpStatus.OK_200.getStatusCode())
        .isPresent();
  }

  private Optional<HttpResponse<String>> requestWithRetry(
      String path, String sessionId, Object requestObject) {
    for (ServerEndpoint serverEndpoint : serverEndpoints) {
      try {
        return request(serverEndpoint, path, sessionId, requestObject);
      } catch (Exception ex) {
        terminalService.printSystemMessage("Retry with the next server node.");
      }
    }
    terminalService.printSystemMessage("Request failed.");
    return Optional.empty();
  }

  private Optional<HttpResponse<String>> request(
      ServerEndpoint serverEndpoint, String path, String sessionId, Object requestObject)
      throws Exception {
    try {
      String url = "http://%s:%s".formatted(serverEndpoint.address(), serverEndpoint.port());
      HttpRequest.Builder builder =
          HttpRequest.newBuilder()
              .uri(new URI(url + path))
              .header("Content-Type", "application/json");
      if (!sessionId.isEmpty()) {
        builder.header("Cookie", "SESSION=" + sessionId);
      }
      if (requestObject != null) {
        JsonUtil.toJson(requestObject)
            .ifPresent(jsonBody -> builder.POST(HttpRequest.BodyPublishers.ofString(jsonBody)));
      } else {
        builder.POST(HttpRequest.BodyPublishers.noBody());
      }

      HttpResponse<String> httpResponse =
          HttpClient.newHttpClient().send(builder.build(), HttpResponse.BodyHandlers.ofString());
      terminalService.printSystemMessage(
          "Response Status: %d, Body: %s"
              .formatted(httpResponse.statusCode(), httpResponse.body()));
      return Optional.of(httpResponse);
    } catch (Exception ex) {
      terminalService.printSystemMessage("API call failed. cause: %s".formatted(ex.getMessage()));
      throw ex;
    }
  }

  private void prepareServerEndpoints(List<String> serviceDiscoveryUrls, String path) {
    for (String serviceDiscoveryUrl : serviceDiscoveryUrls) {
      try {
        prepareServerEndpoints("http://%s%s".formatted(serviceDiscoveryUrl, path));
        return;
      } catch (Exception ex) {
        terminalService.printSystemMessage("Retry with the next discovery node.");
      }
    }
  }

  private void prepareServerEndpoints(String serviceDiscoveryUrl) throws Exception {
    try {
      HttpRequest.Builder builder =
          HttpRequest.newBuilder().uri(new URI(serviceDiscoveryUrl)).GET();
      HttpResponse<String> httpResponse =
          HttpClient.newHttpClient().send(builder.build(), HttpResponse.BodyHandlers.ofString());

      ObjectMapper mapper = new ObjectMapper();
      JsonNode nodes = mapper.readTree(httpResponse.body());
      for (JsonNode node : nodes) {
        String serviceAddress = node.get("ServiceAddress").asText();
        String servicePort = node.get("ServicePort").asText();
        serverEndpoints.add(new ServerEndpoint(serviceAddress, servicePort));
      }
      Collections.shuffle(serverEndpoints);
      terminalService.printSystemMessage(
          "Discovery serverEndpoints=%s".formatted(serverEndpoints.toString()));
    } catch (Exception ex) {
      terminalService.printSystemMessage(
          "Discovery api call failed. cause: %s".formatted(ex.getMessage()));
      throw ex;
    }
  }
}
