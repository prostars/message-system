package net.prostars.messagesystem.handler.kafka;

import java.util.List;
import net.prostars.messagesystem.dto.domain.Connection;
import net.prostars.messagesystem.dto.domain.UserId;
import net.prostars.messagesystem.dto.kafka.FetchConnectionsRequestRecord;
import net.prostars.messagesystem.dto.kafka.FetchConnectionsResponseRecord;
import net.prostars.messagesystem.service.ClientNotificationService;
import net.prostars.messagesystem.service.UserConnectionService;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("unused")
public class FetchConnectionsRequestRecordHandler
    implements BaseRecordHandler<FetchConnectionsRequestRecord> {

  private final UserConnectionService userConnectionService;
  private final ClientNotificationService clientNotificationService;

  public FetchConnectionsRequestRecordHandler(
      UserConnectionService userConnectionService,
      ClientNotificationService clientNotificationService) {
    this.userConnectionService = userConnectionService;
    this.clientNotificationService = clientNotificationService;
  }

  @Override
  public void handleRecord(FetchConnectionsRequestRecord record) {
    UserId senderUserId = record.userId();
    List<Connection> connections =
        userConnectionService.getUsersByStatus(senderUserId, record.status()).stream()
            .map(user -> new Connection(user.username(), record.status()))
            .toList();
    clientNotificationService.sendMessage(
        senderUserId, new FetchConnectionsResponseRecord(senderUserId, connections));
  }
}
