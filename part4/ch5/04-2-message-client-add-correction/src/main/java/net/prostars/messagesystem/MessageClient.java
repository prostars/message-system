package net.prostars.messagesystem;

import java.io.IOException;
import net.prostars.messagesystem.dto.websocket.outbound.WriteMessage;
import net.prostars.messagesystem.handler.CommandHandler;
import net.prostars.messagesystem.handler.InboundMessageHandler;
import net.prostars.messagesystem.handler.WebSocketMessageHandler;
import net.prostars.messagesystem.service.*;
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
    MessageService messageService = new MessageService(userService, terminalService);
    InboundMessageHandler inboundMessageHandler =
        new InboundMessageHandler(userService, messageService, terminalService);
    RestApiService restApiService = new RestApiService(terminalService, BASE_URL);
    WebSocketService webSocketService =
        new WebSocketService(
            userService, terminalService, messageService, BASE_URL, WEBSOCKET_ENDPOINT);
    webSocketService.setWebSocketMessageHandler(new WebSocketMessageHandler(inboundMessageHandler));
    CommandHandler commandHandler =
        new CommandHandler(userService, restApiService, webSocketService, terminalService);
    messageService.setWebSocketService(webSocketService);

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
          webSocketService.sendMessage(
              new WriteMessage(System.currentTimeMillis(), userService.getChannelId(), input));
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
