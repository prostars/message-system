package net.prostars.messagesystem.config;

import com.zaxxer.hikari.HikariDataSource;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import net.prostars.messagesystem.database.RoutingDataSource;
import net.prostars.messagesystem.database.ShardContext;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

@Configuration
public class DataSourceConfig {

  @Bean
  @ConfigurationProperties("spring.datasource.node1")
  public DataSource node1DataSource() {
    return DataSourceBuilder.create().type(HikariDataSource.class).build();
  }

  @Bean
  @ConfigurationProperties("spring.datasource.node2")
  public DataSource node2DataSource() {
    return DataSourceBuilder.create().type(HikariDataSource.class).build();
  }

  @Primary
  @Bean
  public DataSource routingDataSource(DataSource node1DataSource, DataSource node2DataSource) {
    RoutingDataSource routingDataSource = new RoutingDataSource();
    Map<Object, Object> targetDataSources = new HashMap<>();
    targetDataSources.put("node1", node1DataSource);
    targetDataSources.put("node2", node2DataSource);
    routingDataSource.setTargetDataSources(targetDataSources);

    ShardContext.setChannelId(1L);
    routingDataSource.afterPropertiesSet();
    return routingDataSource;
  }

  @Bean
  public DataSourceInitializer node1Initializer(DataSource node1DataSource) {
    return dataSourceInitializer(node1DataSource, 1L);
  }

  @Bean
  public DataSourceInitializer node2Initializer(DataSource node1DataSource) {
    return dataSourceInitializer(node1DataSource, 2L);
  }

  @EventListener
  public void handleContextRefreshedEvent(ContextRefreshedEvent event) {
    ShardContext.clear();
  }

  private DataSourceInitializer dataSourceInitializer(DataSource dataSource, Long channelId) {
    DataSourceInitializer initializer = new DataSourceInitializer();
    initializer.setDataSource(dataSource);

    ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
    populator.addScript(new ClassPathResource("message.sql"));

    ShardContext.setChannelId(channelId);
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
