package net.prostars.messagesystem.dto.websocket.inbound;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import net.prostars.messagesystem.constant.MessageType;

public class CreateRequest extends BaseRequest {

  private final String title;
  private final List<String> participantUsernames;

  @JsonCreator
  public CreateRequest(
      @JsonProperty("title") String title,
      @JsonProperty("participantUsernames") List<String> participantUsernames) {
    super(MessageType.CREATE_REQUEST);
    this.title = title;
    this.participantUsernames = participantUsernames;
  }

  public String getTitle() {
    return title;
  }

  public List<String> getParticipantUsernames() {
    return participantUsernames;
  }
}
