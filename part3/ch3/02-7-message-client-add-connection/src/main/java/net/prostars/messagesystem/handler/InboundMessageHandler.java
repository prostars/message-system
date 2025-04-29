package net.prostars.messagesystem.handler;

import net.prostars.messagesystem.dto.websocket.inbound.*;
import net.prostars.messagesystem.json.JsonUtil;
import net.prostars.messagesystem.service.TerminalService;

public class InboundMessageHandler {

  private final TerminalService terminalService;

  public InboundMessageHandler(TerminalService terminalService) {
    this.terminalService = terminalService;
  }

  public void handle(String payload) {
    JsonUtil.fromJson(payload, BaseMessage.class)
        .ifPresent(
            message -> {
              if (message instanceof MessageNotification messageNotification) {
                message(messageNotification);
              } else if (message
                  instanceof FetchUserInvitecodeResponse fetchUserInvitecodeResponse) {
                fetchUserInviteCode(fetchUserInvitecodeResponse);
              } else if (message instanceof InviteResponse inviteResponse) {
                invite(inviteResponse);
              } else if (message instanceof InviteNotification inviteResponse) {
                askInvite(inviteResponse);
              } else if (message instanceof AcceptResponse acceptResponse) {
                accept(acceptResponse);
              } else if (message instanceof AcceptNotification acceptNotification) {
                acceptNotification(acceptNotification);
              } else if (message instanceof RejectResponse rejectResponse) {
                reject(rejectResponse);
              } else if (message instanceof DisconnectResponse disconnectResponse) {
                disconnect(disconnectResponse);
              } else if (message instanceof FetchConnectionsResponse fetchConnectionsResponse) {
                fetchConnections(fetchConnectionsResponse);
              }
            });
  }

  private void message(MessageNotification messageNotification) {
    terminalService.printMessage(
        messageNotification.getUsername(), messageNotification.getContent());
  }

  private void fetchUserInviteCode(FetchUserInvitecodeResponse fetchUserInvitecodeResponse) {
    terminalService.printSystemMessage(
        "My InviteCode: %s".formatted(fetchUserInvitecodeResponse.getInviteCode()));
  }

  private void invite(InviteResponse inviteResponse) {
    terminalService.printSystemMessage(
        "Invite %s result: %s"
            .formatted(inviteResponse.getInviteCode(), inviteResponse.getStatus()));
  }

  private void askInvite(InviteNotification inviteNotification) {
    terminalService.printSystemMessage(
        "Do you accept %s's connection request?".formatted(inviteNotification.getUsername()));
  }

  private void accept(AcceptResponse acceptResponse) {
    terminalService.printSystemMessage("Connected %s".formatted(acceptResponse.getUsername()));
  }

  private void acceptNotification(AcceptNotification acceptNotification) {
    terminalService.printSystemMessage("Connected %s".formatted(acceptNotification.getUsername()));
  }

  private void reject(RejectResponse rejectResponse) {
    terminalService.printSystemMessage(
        "Reject %s result: %s".formatted(rejectResponse.getUsername(), rejectResponse.getStatus()));
  }

  private void disconnect(DisconnectResponse disconnectResponse) {
    terminalService.printSystemMessage(
        "Disconnected %s result: %s"
            .formatted(disconnectResponse.getUsername(), disconnectResponse.getStatus()));
  }

  private void fetchConnections(FetchConnectionsResponse fetchConnectionsResponse) {
    fetchConnectionsResponse
        .getConnections()
        .forEach(
            connection ->
                terminalService.printSystemMessage(
                    "%s : %s".formatted(connection.username(), connection.status())));
  }
}
