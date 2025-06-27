package net.prostars.messagesystem.config;

import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import net.prostars.messagesystem.database.RoutingDataSource;
import net.prostars.messagesystem.database.ShardContext;
import net.prostars.messagesystem.dto.domain.ChannelId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

@Configuration
@SuppressWarnings("unused")
public class DataSourceConfig {

  private static final Logger log = LoggerFactory.getLogger(DataSourceConfig.class);

  @Bean
  @ConfigurationProperties(prefix = "spring.datasource.source.hikari")
  public DataSource sourceDataSource() {
    return DataSourceBuilder.create().type(HikariDataSource.class).build();
  }

  @Bean
  @ConfigurationProperties(prefix = "spring.datasource.replica.hikari")
  public DataSource replicaDataSource() {
    return DataSourceBuilder.create().type(HikariDataSource.class).build();
  }

  @Bean
  @ConfigurationProperties(prefix = "spring.datasource.source-message1.hikari")
  public DataSource sourceMessage1DataSource() {
    return DataSourceBuilder.create().type(HikariDataSource.class).build();
  }

  @Bean
  @ConfigurationProperties(prefix = "spring.datasource.replica-message1.hikari")
  public DataSource replicaMessage1DataSource() {
    return DataSourceBuilder.create().type(HikariDataSource.class).build();
  }

  @Bean
  @ConfigurationProperties(prefix = "spring.datasource.source-message2.hikari")
  public DataSource sourceMessage2DataSource() {
    return DataSourceBuilder.create().type(HikariDataSource.class).build();
  }

  @Bean
  @ConfigurationProperties(prefix = "spring.datasource.replica-message2.hikari")
  public DataSource replicaMessage2DataSource() {
    return DataSourceBuilder.create().type(HikariDataSource.class).build();
  }

  @Bean
  public DataSource routingDataSource(
      @Qualifier("sourceDataSource") DataSource sourceDataSource,
      @Qualifier("replicaDataSource") DataSource replicaDataSource,
      @Qualifier("sourceMessage1DataSource") DataSource sourceMessage1DataSource,
      @Qualifier("replicaMessage1DataSource") DataSource replicaMessage1DataSource,
      @Qualifier("sourceMessage2DataSource") DataSource sourceMessage2DataSource,
      @Qualifier("replicaMessage2DataSource") DataSource replicaMessage2DataSource)
      throws SQLException {
    RoutingDataSource routingDataSource = new RoutingDataSource();
    Map<Object, Object> targetDataSources = new HashMap<>();
    targetDataSources.put("source", sourceDataSource);
    targetDataSources.put("replica", replicaDataSource);
    targetDataSources.put("sourceMessage1", sourceMessage1DataSource);
    targetDataSources.put("replicaMessage1", replicaMessage1DataSource);
    targetDataSources.put("sourceMessage2", sourceMessage2DataSource);
    targetDataSources.put("replicaMessage2", replicaMessage2DataSource);
    routingDataSource.setTargetDataSources(targetDataSources);

    try (Connection ignored = replicaDataSource.getConnection()) {
      log.info("Init ReplicaConnectionPool.");
    }
    try (Connection ignored = replicaMessage1DataSource.getConnection()) {
      log.info("Init ReplicaMessage1ConnectionPool.");
    }
    try (Connection ignored = replicaMessage2DataSource.getConnection()) {
      log.info("Init ReplicaMessage2ConnectionPool.");
    }
    return routingDataSource;
  }

  @Primary
  @Bean
  public DataSource lazyConnectionDataSource(
      @Qualifier("routingDataSource") DataSource routingDataSource) {
    return new LazyConnectionDataSourceProxy(routingDataSource);
  }

  @Bean
  public DataSourceInitializer sourceDataSourceInitializer(DataSource sourceDataSource) {
    return dataSourceInitializer(sourceDataSource, null);
  }

  @Bean
  public DataSourceInitializer sourceMessage1DataSourceInitializer(
      DataSource sourceMessage1DataSource) {
    return dataSourceInitializer(sourceMessage1DataSource, new ChannelId(1L));
  }

  @Bean
  public DataSourceInitializer sourceMessage2DataSourceInitializer(
      DataSource sourceMessage2DataSource) {
    return dataSourceInitializer(sourceMessage2DataSource, new ChannelId(2L));
  }

  private DataSourceInitializer dataSourceInitializer(DataSource dataSource, ChannelId channelId) {
    DataSourceInitializer initializer = new DataSourceInitializer();
    initializer.setDataSource(dataSource);

    ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
    if (channelId == null) {
      populator.addScript(new ClassPathResource("messagesystem.sql"));
    } else {
      populator.addScript(new ClassPathResource("message.sql"));
      ShardContext.setChannelId(channelId.id());
    }

    DatabasePopulator wrapper =
        connection -> {
          try {
            populator.populate(connection);
          } finally {
            ShardContext.clear();
          }
        };
    initializer.setDatabasePopulator(wrapper);
    return initializer;
  }
}
