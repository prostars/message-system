package net.prostars.messagesystem;

import java.io.IOException;
import net.prostars.messagesystem.dto.websocket.outbound.WriteMessage;
import net.prostars.messagesystem.handler.CommandHandler;
import net.prostars.messagesystem.handler.InboundMessageHandler;
import net.prostars.messagesystem.handler.WebSocketMessageHandler;
import net.prostars.messagesystem.handler.WebSocketSender;
import net.prostars.messagesystem.service.RestApiService;
import net.prostars.messagesystem.service.TerminalService;
import net.prostars.messagesystem.service.UserService;
import net.prostars.messagesystem.service.WebSocketService;
import org.jline.reader.UserInterruptException;

public class MessageClient {

  public static void main(String[] args) {
    final String BASE_URL = "localhost:80";
    final String WEBSOCKET_ENDPOINT = "/ws/v1/message";

    TerminalService terminalService;
    try {
      terminalService = TerminalService.create();
    } catch (IOException ex) {
      System.err.println("Failed to run MessageClient");
      return;
    }

    UserService userService = new UserService();
    InboundMessageHandler inboundMessageHandler =
        new InboundMessageHandler(userService, terminalService);
    RestApiService restApiService = new RestApiService(terminalService, BASE_URL);
    WebSocketSender webSocketSender = new WebSocketSender(terminalService);
    WebSocketService webSocketService =
        new WebSocketService(
            userService, terminalService, webSocketSender, BASE_URL, WEBSOCKET_ENDPOINT);
    webSocketService.setWebSocketMessageHandler(new WebSocketMessageHandler(inboundMessageHandler));
    CommandHandler commandHandler =
        new CommandHandler(userService, restApiService, webSocketService, terminalService);

    terminalService.printSystemMessage("'/help' Help for commands. ex: /help");

    while (true) {
      try {
        String input = terminalService.readLine("Enter message: ");
        if (!input.isEmpty() && input.charAt(0) == '/') {
          String[] parts = input.split(" ", 2);
          String command = parts[0].substring(1);
          String argument = parts.length > 1 ? parts[1] : "";
          if (!commandHandler.process(command, argument)) {
            break;
          }
        } else if (!input.isEmpty() && userService.isInChannel()) {
          terminalService.printMessage("<me>", input);
          webSocketService.sendMessage(new WriteMessage(userService.getChannelId(), input));
        }
      } catch (UserInterruptException ex) {
        terminalService.flush();
        commandHandler.process("exit", "");
        return;
      } catch (NumberFormatException ex) {
        terminalService.printSystemMessage("Invalid Input: " + ex.getMessage());
      }
    }
  }
}
