package net.prostars.messagesystem.dto.websocket.inbound;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import net.prostars.messagesystem.constant.MessageType;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
  @JsonSubTypes.Type(
      value = FetchUserInvitecodeRequest.class,
      name = MessageType.FETCH_USER_INVITECODE_REQUEST),
  @JsonSubTypes.Type(
      value = FetchConnectionsRequest.class,
      name = MessageType.FETCH_CONNECTIONS_REQUEST),
  @JsonSubTypes.Type(value = InviteRequest.class, name = MessageType.INVITE_REQUEST),
  @JsonSubTypes.Type(value = AcceptRequest.class, name = MessageType.ACCEPT_REQUEST),
  @JsonSubTypes.Type(value = RejectRequest.class, name = MessageType.REJECT_REQUEST),
  @JsonSubTypes.Type(value = WriteMessageRequest.class, name = MessageType.WRITE_MESSAGE),
  @JsonSubTypes.Type(value = KeepAliveRequest.class, name = MessageType.KEEP_ALIVE)
})
public abstract class BaseRequest {
  private final String type;

  public BaseRequest(String type) {
    this.type = type;
  }

  public String getType() {
    return type;
  }
}
