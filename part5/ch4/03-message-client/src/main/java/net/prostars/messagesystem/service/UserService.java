package net.prostars.messagesystem.service;

import net.prostars.messagesystem.dto.domain.ChannelId;
import net.prostars.messagesystem.dto.domain.Message;
import net.prostars.messagesystem.dto.domain.MessageSeqId;

import java.util.TreeSet;

public class UserService {

  private Location userLocation = Location.LOBBY;
  private String username = "";
  private ChannelId channelId = null;
  private volatile MessageSeqId lastReadMessageSeqId = null;
  private final TreeSet<Message> messageBuffer = new TreeSet<>(); 

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
  
  public MessageSeqId getLastReadMessageSeqId() {
    return lastReadMessageSeqId;
  }

  public synchronized void setLastReadMessageSeqId(MessageSeqId lastReadMessageSeqId) {
    if (getLastReadMessageSeqId() == null
        || lastReadMessageSeqId == null
        || getLastReadMessageSeqId().id() < lastReadMessageSeqId.id()) {
      this.lastReadMessageSeqId = lastReadMessageSeqId;
    }
  }
  
  public boolean isBufferEmpty() {
    return messageBuffer.isEmpty();
  }
  
  public int getBufferSize() {
    return messageBuffer.size();
  }
  
  public Message peekMessage() {
    return messageBuffer.first();
  }
  
  public Message popMessage() {
    return messageBuffer.pollFirst();
  }
  
  public void addMessage(Message message) {
    messageBuffer.add(message);
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
    setLastReadMessageSeqId(null);
    messageBuffer.clear();
  }

  public void moveToChannel(ChannelId channelId) {
    userLocation = Location.CHANNEL;
    this.channelId = channelId;
    setLastReadMessageSeqId(null);
    messageBuffer.clear();
  }

  private enum Location {
    LOBBY,
    CHANNEL;
  }
}
