package net.prostars.messagesystem.service;

import net.prostars.messagesystem.dto.domain.ChannelId;

public class UserService {

  private Location userLocation = Location.LOBBY;
  private String username = "";
  private ChannelId channelId = null;

  public boolean isInLobby() {
    return userLocation == Location.LOBBY;
  }

  public boolean isInChannel() {
    return userLocation == Location.CHANNEL;
  }

  public String getUsername() {
    return username;
  }

  public ChannelId getChannelId() {
    return channelId;
  }

  public void login(String username) {
    this.username = username;
    moveToLobby();
  }

  public void logout() {
    this.username = "";
    moveToLobby();
  }

  public void moveToLobby() {
    userLocation = Location.LOBBY;
    this.channelId = null;
  }

  public void moveToChannel(ChannelId channelId) {
    userLocation = Location.CHANNEL;
    this.channelId = channelId;
  }

  private enum Location {
    LOBBY,
    CHANNEL;
  }
}
