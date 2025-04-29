package net.prostars.messagesystem.dto.websocket.inbound;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.prostars.messagesystem.constant.MessageType;

public class CreateRequest extends BaseRequest {

  private final String title;
  private final String participantUsername;

  @JsonCreator
  public CreateRequest(
      @JsonProperty("title") String title,
      @JsonProperty("participantUsername") String participantUsername) {
    super(MessageType.CREATE_REQUEST);
    this.title = title;
    this.participantUsername = participantUsername;
  }

  public String getTitle() {
    return title;
  }

  public String getParticipantUsername() {
    return participantUsername;
  }
}
