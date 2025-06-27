package net.prostars.messagesystem.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class RoutingDataSource extends AbstractRoutingDataSource {

  private static final Logger log = LoggerFactory.getLogger(RoutingDataSource.class);

  @Override
  protected Object determineCurrentLookupKey() {
    String dataSourceKey = ShardContext.getChannelId() % 2 == 0 ? "node2" : "node1";
    log.info("Routing to {} dataSource", dataSourceKey);
    return dataSourceKey;
  }
}
