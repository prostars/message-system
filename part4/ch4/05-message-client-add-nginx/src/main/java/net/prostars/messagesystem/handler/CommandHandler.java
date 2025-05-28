package net.prostars.messagesystem.handler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import net.prostars.messagesystem.constant.UserConnectionStatus;
import net.prostars.messagesystem.dto.domain.ChannelId;
import net.prostars.messagesystem.dto.domain.InviteCode;
import net.prostars.messagesystem.dto.websocket.outbound.*;
import net.prostars.messagesystem.service.RestApiService;
import net.prostars.messagesystem.service.TerminalService;
import net.prostars.messagesystem.service.UserService;
import net.prostars.messagesystem.service.WebSocketService;

public class CommandHandler {

  private final UserService userService;
  private final RestApiService restApiService;
  private final WebSocketService webSocketService;
  private final TerminalService terminalService;
  private final Map<String, Function<String[], Boolean>> commands = new HashMap<>();

  public CommandHandler(
      UserService userService,
      RestApiService restApiService,
      WebSocketService webSocketService,
      TerminalService terminalService) {
    this.userService = userService;
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
    commands.put("invitecode", this::invitecode);
    commands.put("invite", this::invite);
    commands.put("accept", this::accept);
    commands.put("reject", this::reject);
    commands.put("disconnect", this::disconnect);
    commands.put("connections", this::connections);
    commands.put("pending", this::pending);
    commands.put("channels", this::channels);
    commands.put("create", this::create);
    commands.put("join", this::join);
    commands.put("enter", this::enter);
    commands.put("leave", this::leave);
    commands.put("quit", this::quit);
    commands.put("clear", this::clear);
    commands.put("exit", this::exit);
    commands.put("help", this::help);
  }

  private Boolean register(String[] params) {
    if (userService.isInLobby() && params.length > 1) {
      if (restApiService.register(params[0], params[1])) {
        terminalService.printSystemMessage("Registered.");
      } else {
        terminalService.printSystemMessage("Register failed.");
      }
    }
    return true;
  }

  private Boolean unregister(String[] params) {
    if (userService.isInLobby()) {
      webSocketService.closeSession();
      if (restApiService.unregister()) {
        terminalService.printSystemMessage("Unregistered.");
      } else {
        terminalService.printSystemMessage("Unregister failed.");
      }
    }
    return true;
  }

  private Boolean login(String[] params) {
    if (userService.isInLobby() && params.length > 1) {
      if (restApiService.login(params[0], params[1])) {
        if (webSocketService.createSession(restApiService.getSessionId())) {
          userService.login(params[0]);
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
      userService.logout();
      terminalService.printSystemMessage("Logout successful.");
    } else {
      terminalService.printSystemMessage("Logout failed.");
    }
    return true;
  }

  private Boolean invitecode(String[] params) {
    if (userService.isInLobby() && params.length > 0) {
      if ("user".equals(params[0])) {
        webSocketService.sendMessage(new FetchUserInvitecodeRequest());
        terminalService.printSystemMessage("Get invitecode for mine.");
      } else if ("channel".equals(params[0]) && params.length > 1) {
        webSocketService.sendMessage(
            new FetchChannelInviteCodeRequest(new ChannelId(Long.valueOf(params[1]))));
        terminalService.printSystemMessage("Get invitecode for channel.");
      }
    }
    return true;
  }

  private Boolean invite(String[] params) {
    if (userService.isInLobby() && params.length > 0) {
      webSocketService.sendMessage(new InviteRequest(new InviteCode(params[0])));
      terminalService.printSystemMessage("Invite user.");
    }
    return true;
  }

  private Boolean accept(String[] params) {
    if (userService.isInLobby() && params.length > 0) {
      webSocketService.sendMessage(new AcceptRequest(params[0]));
      terminalService.printSystemMessage("Accept user invite.");
    }
    return true;
  }

  private Boolean reject(String[] params) {
    if (userService.isInLobby() && params.length > 0) {
      webSocketService.sendMessage(new RejectRequest(params[0]));
      terminalService.printSystemMessage("Reject user invite.");
    }
    return true;
  }

  private Boolean disconnect(String[] params) {
    if (userService.isInLobby() && params.length > 0) {
      webSocketService.sendMessage(new DisconnectRequest(params[0]));
      terminalService.printSystemMessage("Disconnect user.");
    }
    return true;
  }

  private Boolean connections(String[] params) {
    if (userService.isInLobby()) {
      webSocketService.sendMessage(new FetchConnectionsRequest(UserConnectionStatus.ACCEPTED));
      terminalService.printSystemMessage("Get connection list.");
    }
    return true;
  }

  private Boolean pending(String[] params) {
    if (userService.isInLobby()) {
      webSocketService.sendMessage(new FetchConnectionsRequest(UserConnectionStatus.PENDING));
      terminalService.printSystemMessage("Get pending list.");
    }
    return true;
  }

  private Boolean channels(String[] params) {
    if (userService.isInLobby()) {
      webSocketService.sendMessage(new FetchChannelsRequest());
      terminalService.printSystemMessage("Request channels.");
    }
    return true;
  }

  private Boolean create(String[] params) {
    if (userService.isInLobby() && params.length > 1 && params.length < 100) {
      webSocketService.sendMessage(
          new CreateRequest(params[0], List.of(Arrays.copyOfRange(params, 1, params.length))));
      terminalService.printSystemMessage("Request create channel.");
    } else {
      terminalService.printSystemMessage("Only 1 to 99 users can be included.");
    }
    return true;
  }

  private Boolean join(String[] params) {
    if (userService.isInLobby() && params.length > 0) {
      webSocketService.sendMessage(new JoinRequest(new InviteCode(params[0])));
      terminalService.printSystemMessage("Request join channel.");
    }
    return true;
  }

  private Boolean enter(String[] params) {
    if (userService.isInLobby() && params.length > 0) {
      webSocketService.sendMessage(new EnterRequest(new ChannelId(Long.valueOf(params[0]))));
      terminalService.printSystemMessage("Request enter channel.");
    }
    return true;
  }

  private Boolean leave(String[] params) {
    if (userService.isInChannel()) {
      webSocketService.sendMessage(new LeaveRequest());
      terminalService.printSystemMessage("Request leave channel.");
    }
    return true;
  }

  private Boolean quit(String[] params) {
    if (userService.isInLobby() && params.length > 0) {
      webSocketService.sendMessage(new QuitRequest(new ChannelId(Long.valueOf(params[0]))));
      terminalService.printSystemMessage("Request quit channel.");
    }
    return true;
  }

  private Boolean clear(String[] params) {
    terminalService.clearTerminal();
    terminalService.printSystemMessage("Terminal cleared.");
    return true;
  }

  private Boolean exit(String[] params) {
    logout(params);
    terminalService.printSystemMessage("Exit message client.");
    return false;
  }

  private Boolean help(String[] params) {
    terminalService.printSystemMessage(
        """
        Commands For Lobby
        '/register' Register a new user. ex: /register <Username> <Password>
        '/unregister' Unregister current user. ex: /unregister
        '/login' Login. ex: /login <Username> <Password>
        '/invitecode' Get the InviteCode of mine or joined channel. ex: /invitecode user or /invitecode channel <ChannelId>
        '/invite' Invite a user to connect. ex: /invite <InviteCode>
        '/accept' Accept the invite request received. ex: /accept <InviterUsername>
        '/reject' Reject the invite request received. ex: /reject <InviterUsername>
        '/disconnect' Disconnect user. ex: /disconnect <ConnectedUsername>
        '/connections' View the list of connected users. ex: /connections
        '/pending' View the list of pending invites. ex: /pending
        '/channels' View the list of joined channels users. ex: /channels
        '/create' Create a channel. (Up to 99 users) ex: /create <Title> <Username1> ...
        '/join' Join the channel. ex: /join <InviteCode>
        '/enter' Enter the channel. ex: /enter <ChannelId>
        '/quit' Quit the channel. ex: /quit <ChannelId>

        Commands For Channel
        '/leave' Leave the channel. ex: /leave

        Commands For Lobby/Channel
        '/logout' Logout. ex: /logout
        '/clear' Clear the terminal. ex: /clear
        '/exit' Exit the client. ex: /exit
        """);
    return true;
  }
}
