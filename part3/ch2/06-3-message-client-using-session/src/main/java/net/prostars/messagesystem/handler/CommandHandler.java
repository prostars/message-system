package net.prostars.messagesystem.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import net.prostars.messagesystem.service.RestApiService;
import net.prostars.messagesystem.service.TerminalService;
import net.prostars.messagesystem.service.WebSocketService;

public class CommandHandler {

  private final RestApiService restApiService;
  private final WebSocketService webSocketService;
  private final TerminalService terminalService;
  private final Map<String, Function<String[], Boolean>> commands = new HashMap<>();

  public CommandHandler(
      RestApiService restApiService,
      WebSocketService webSocketService,
      TerminalService terminalService) {
    this.restApiService = restApiService;
    this.webSocketService = webSocketService;
    this.terminalService = terminalService;
    prepareCommands();
  }

  public boolean process(String command, String argument) {
    Function<String[], Boolean> commander =
        commands.getOrDefault(
            command,
            (ignored) -> {
              terminalService.printSystemMessage("Invalid command: %s".formatted(command));
              return true;
            });
    return commander.apply(argument.split(" "));
  }

  private void prepareCommands() {
    commands.put("register", this::register);
    commands.put("unregister", this::unregister);
    commands.put("login", this::login);
    commands.put("logout", this::logout);
    commands.put("clear", this::clear);
    commands.put("exit", this::exit);
  }

  private Boolean register(String[] params) {
    if (params.length > 1) {
      if (restApiService.register(params[0], params[1])) {
        terminalService.printSystemMessage("Registered.");
      } else {
        terminalService.printSystemMessage("Register failed.");
      }
    }
    return true;
  }

  private Boolean unregister(String[] params) {
    webSocketService.closeSession();
    if (restApiService.unregister()) {
      terminalService.printSystemMessage("Unregistered.");
    } else {
      terminalService.printSystemMessage("Unregister failed.");
    }
    return true;
  }

  private Boolean login(String[] params) {
    if (params.length > 1) {
      if (restApiService.login(params[0], params[1])) {
        if (webSocketService.createSession(restApiService.getSessionId())) {
          terminalService.printSystemMessage("Login successful.");
        }
      } else {
        terminalService.printSystemMessage("Login failed.");
      }
    }
    return true;
  }

  private Boolean logout(String[] params) {
    webSocketService.closeSession();
    if (restApiService.logout()) {
      terminalService.printSystemMessage("Logout successful.");
    } else {
      terminalService.printSystemMessage("Logout failed.");
    }
    return true;
  }

  private Boolean clear(String[] params) {
    terminalService.clearTerminal();
    terminalService.printSystemMessage("Terminal cleared.");
    return true;
  }

  private Boolean exit(String[] params) {
    webSocketService.closeSession();
    terminalService.printSystemMessage("Exit message client.");
    return false;
  }
}
