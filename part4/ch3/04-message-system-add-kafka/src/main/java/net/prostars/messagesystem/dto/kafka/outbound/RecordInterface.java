package net.prostars.messagesystem.dto.kafka.outbound;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import net.prostars.messagesystem.constant.MessageType;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
  @JsonSubTypes.Type(value = InviteResponseRecord.class, name = MessageType.INVITE_RESPONSE),
  @JsonSubTypes.Type(value = AcceptResponseRecord.class, name = MessageType.ACCEPT_RESPONSE),
  @JsonSubTypes.Type(value = RejectResponseRecord.class, name = MessageType.REJECT_RESPONSE),
  @JsonSubTypes.Type(
      value = DisconnectResponseRecord.class,
      name = MessageType.DISCONNECT_RESPONSE),
  @JsonSubTypes.Type(value = CreateResponseRecord.class, name = MessageType.CREATE_RESPONSE),
  @JsonSubTypes.Type(value = QuitResponseRecord.class, name = MessageType.QUIT_RESPONSE),
  @JsonSubTypes.Type(value = InviteNotificationRecord.class, name = MessageType.ASK_INVITE),
  @JsonSubTypes.Type(value = JoinNotificationRecord.class, name = MessageType.NOTIFY_JOIN),
  @JsonSubTypes.Type(value = AcceptNotificationRecord.class, name = MessageType.NOTIFY_ACCEPT),
  @JsonSubTypes.Type(value = MessageNotificationRecord.class, name = MessageType.NOTIFY_MESSAGE),
})
public interface RecordInterface {
  String type();
}
