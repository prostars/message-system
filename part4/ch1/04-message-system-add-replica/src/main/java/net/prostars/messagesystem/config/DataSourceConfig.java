package net.prostars.messagesystem.config;

import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import net.prostars.messagesystem.database.RoutingDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;

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
  public DataSource routingDataSource(
      @Qualifier("sourceDataSource") DataSource sourceDataSource,
      @Qualifier("replicaDataSource") DataSource replicaDataSource)
      throws SQLException {
    RoutingDataSource routingDataSource = new RoutingDataSource();
    Map<Object, Object> targetDataSources = new HashMap<>();
    targetDataSources.put("source", sourceDataSource);
    targetDataSources.put("replica", replicaDataSource);
    routingDataSource.setTargetDataSources(targetDataSources);
    routingDataSource.setDefaultTargetDataSource(sourceDataSource);

    try (Connection ignored = replicaDataSource.getConnection()) {
      log.info("Init ReplicaConnectionPool.");
    }
    return routingDataSource;
  }

  @Primary
  @Bean
  public DataSource lazyConnectionDataSource(
      @Qualifier("routingDataSource") DataSource routingDataSource) {
    return new LazyConnectionDataSourceProxy(routingDataSource);
  }
}
