package br.com.challenge.config;

import com.redis.testcontainers.RedisContainer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistrar;
import org.testcontainers.mysql.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class TestContainerConfig {

  private static final MySQLContainer MY_SQL_CONTAINER =
      new MySQLContainer(DockerImageName.parse("mysql:8.0.34"));

  private static final RedisContainer REDIS_CONTAINER =
      new RedisContainer(DockerImageName.parse("redis:8.6.3-alpine")).withExposedPorts(6379);

  static {
    MY_SQL_CONTAINER.start();
    REDIS_CONTAINER.start();
  }

  @Bean
  public MySQLContainer mySQLDbContainer() {
    return MY_SQL_CONTAINER;
  }

  @Bean
  public RedisContainer redisContainer() {
    return REDIS_CONTAINER;
  }

  @Bean
  public static DynamicPropertyRegistrar mySQLDbProperties(MySQLContainer mySQLContainer) {
    return properties -> {
      properties.add("spring.datasource.username", mySQLContainer::getUsername);
      properties.add("spring.datasource.password", mySQLContainer::getPassword);
      properties.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
      properties.add("spring.jpa.show-sql", () -> "true");
    };
  }

  @Bean
  public static DynamicPropertyRegistrar redisProperties(
      @Qualifier("redisContainer") RedisContainer container) {
    return properties -> {
      properties.add("spring.data.redis.host", container::getHost);
      properties.add("spring.data.redis.port", () -> container.getMappedPort(6379));
      properties.add("spring.data.redis.password", () -> "");
      properties.add("spring.data.redis.database", () -> "0");
    };
  }
}
