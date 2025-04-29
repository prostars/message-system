package net.prostars.messagesystem;

import java.io.IOException;
import net.prostars.messagesystem.dto.websocket.outbound.WriteMessageRequest;
import net.prostars.messagesystem.handler.CommandHandler;
import net.prostars.messagesystem.handler.InboundMessageHandler;
import net.prostars.messagesystem.handler.WebSocketMessageHandler;
import net.prostars.messagesystem.handler.WebSocketSender;
import net.prostars.messagesystem.service.RestApiService;
import net.prostars.messagesystem.service.TerminalService;
import net.prostars.messagesystem.service.WebSocketService;

public class MessageClient {

  public static void main(String[] args) {
    final String BASE_URL = "localhost:8080";
    final String WEBSOCKET_ENDPOINT = "/ws/v1/message";

    TerminalService terminalService;
    try {
      terminalService = TerminalService.create();
    } catch (IOException ex) {
      System.err.println("Failed to run MessageClient");
      return;
    }

    InboundMessageHandler inboundMessageHandler = new InboundMessageHandler(terminalService);
    RestApiService restApiService = new RestApiService(terminalService, BASE_URL);
    WebSocketSender webSocketSender = new WebSocketSender(terminalService);
    WebSocketService webSocketService =
        new WebSocketService(terminalService, webSocketSender, BASE_URL, WEBSOCKET_ENDPOINT);
    webSocketService.setWebSocketMessageHandler(new WebSocketMessageHandler(inboundMessageHandler));
    CommandHandler commandHandler =
        new CommandHandler(restApiService, webSocketService, terminalService);

    while (true) {
      String input = terminalService.readLine("Enter message: ");
      if (!input.isEmpty() && input.charAt(0) == '/') {
        String[] parts = input.split(" ", 2);
        String command = parts[0].substring(1);
        String argument = parts.length > 1 ? parts[1] : "";
        if (!commandHandler.process(command, argument)) {
          break;
        }
      } else if (!input.isEmpty()) {
        terminalService.printMessage("<me>", input);
        webSocketService.sendMessage(new WriteMessageRequest("test client", input));
      }
    }
  }
}
