package br.com.challenge.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private static final String[] NO_AUTH_URLS = {"/actuator/**", "/health/**"};

  private final StaticTokenAuthFilter staticTokenAuthFilter;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) {
    http.csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers(NO_AUTH_URLS)
                    .permitAll()
                    // Swagger endpoints require Basic Auth
                    .requestMatchers("/swagger-ui/**", "/v3/api-docs/**")
                    .authenticated()
                    // All other endpoints use token-based auth
                    .anyRequest()
                    .permitAll())
        // Add Basic Auth support (for Swagger only)
        .httpBasic(Customizer.withDefaults())
        // Add your custom token filter
        .addFilterBefore(staticTokenAuthFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
