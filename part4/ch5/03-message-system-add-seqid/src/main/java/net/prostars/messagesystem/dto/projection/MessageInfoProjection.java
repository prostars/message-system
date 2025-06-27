package net.prostars.messagesystem.dto.projection;

public interface MessageInfoProjection {

  Long getMessageSequence();

  Long getUserId();

  String getContent();
}
