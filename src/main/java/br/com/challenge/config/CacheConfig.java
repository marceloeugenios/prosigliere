package br.com.challenge.config;

import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.tracing.MicrometerTracing;
import io.micrometer.observation.ObservationRegistry;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;

@Configuration
public class CacheConfig {

  @Value("${spring.data.redis.host}")
  private String redisHost;

  @Value("${spring.data.redis.database}")
  private Integer database;

  @Value("${spring.data.redis.port}")
  private int redisPort;

  @Value("${spring.data.redis.password}")
  private String redisPass;

  @Bean
  RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(connectionFactory);
    template.setDefaultSerializer(new JacksonJsonRedisSerializer<>(Object.class));
    return template;
  }

  @Bean
  ClientResources clientResources(ObservationRegistry observationRegistry) {
    return ClientResources.builder()
        .tracing(new MicrometerTracing(observationRegistry, "challenge-redis"))
        .build();
  }

  @Bean
  LettuceConnectionFactory redisConnectionFactory(ClientResources clientResources) {
    final RedisStandaloneConfiguration redisStandaloneConfiguration =
        new RedisStandaloneConfiguration();
    redisStandaloneConfiguration.setDatabase(database);
    redisStandaloneConfiguration.setHostName(redisHost);
    redisStandaloneConfiguration.setPort(redisPort);
    redisStandaloneConfiguration.setPassword(redisPass);
    final LettuceClientConfiguration clientConfig =
        LettuceClientConfiguration.builder().clientResources(clientResources).build();
    final LettuceConnectionFactory lcf =
        new LettuceConnectionFactory(redisStandaloneConfiguration, clientConfig);
    lcf.afterPropertiesSet();
    return lcf;
  }

  @Bean
  RedisCacheManager redisCacheManagerWith10mTtl(LettuceConnectionFactory lettuceConnectionFactory) {
    return getRedisCacheManager(Duration.ofMinutes(10), lettuceConnectionFactory);
  }

  private RedisCacheManager getRedisCacheManager(
      final Duration duration, final LettuceConnectionFactory lettuceConnectionFactory) {
    RedisCacheConfiguration redisCacheConfiguration =
        RedisCacheConfiguration.defaultCacheConfig().entryTtl(duration);
    return RedisCacheManager.RedisCacheManagerBuilder.fromConnectionFactory(
            lettuceConnectionFactory)
        .cacheDefaults(redisCacheConfiguration)
        .build();
  }
}
