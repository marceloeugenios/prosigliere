package br.com.challenge.config;

import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.aop.ObservedAspect;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

  public static final String API_BLOG_HEADER = "API-BLOG";

  @Value("${application.title}")
  private String title;

  @Value("${application.version}")
  private String version;

  @Value("${server.port}")
  private int localServerPort;

  @Value("${app.challenge.url}")
  private String url;

  @Value("${server.servlet.context-path}")
  private String context;

  @Bean
  OpenAPI openApi() {
    var server = new Server();
    if (url.contains("localhost")) {
      server.setUrl(url + ":" + localServerPort + context);
    } else {
      server.setUrl(url + context);
    }
    return new OpenAPI()
        .info(new Info().title(title).version(version))
        .addSecurityItem(new SecurityRequirement().addList(API_BLOG_HEADER))
        .components(
            new io.swagger.v3.oas.models.Components()
                .addSecuritySchemes(
                    API_BLOG_HEADER,
                    new SecurityScheme()
                        .name(API_BLOG_HEADER)
                        .type(SecurityScheme.Type.APIKEY)
                        .in(SecurityScheme.In.HEADER)))
        .servers(List.of(server));
  }

  @Bean
  ObservedAspect observedAspect(ObservationRegistry observationRegistry) {
    return new ObservedAspect(observationRegistry);
  }
}
