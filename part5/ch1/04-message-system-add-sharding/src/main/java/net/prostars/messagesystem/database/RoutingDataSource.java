package net.prostars.messagesystem.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class RoutingDataSource extends AbstractRoutingDataSource {

  private static final Logger log = LoggerFactory.getLogger(RoutingDataSource.class);

  @Override
  protected Object determineCurrentLookupKey() {
    String dataSourceKey =
        TransactionSynchronizationManager.isCurrentTransactionReadOnly() ? "replica" : "source";
    Long channelId = ShardContext.getChannelId();
    if (channelId != null) {
      dataSourceKey += channelId % 2 == 0 ? "Message2" : "Message1";
    }
    
    log.info("Routing to {} dataSource", dataSourceKey);
    return dataSourceKey;
  }
}
