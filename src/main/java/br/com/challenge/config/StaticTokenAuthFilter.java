package br.com.challenge.config;

import static br.com.challenge.config.AppConfig.API_BLOG_HEADER;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class StaticTokenAuthFilter extends OncePerRequestFilter {

  private static final Set<String> EXCLUDED_PATHS =
      Set.of("/swagger-ui", "/v3/api-docs", "/health/check", "/pyroscope", "/actuator");

  private final String apiToken;

  public StaticTokenAuthFilter(@Value("${app.auth.api-token}") String apiToken) {
    this.apiToken = apiToken;
  }

  @Override
  protected void doFilterInternal(
      final HttpServletRequest request,
      final HttpServletResponse response,
      final FilterChain filterChain)
      throws ServletException, IOException {

    if (EXCLUDED_PATHS.stream().anyMatch(path -> request.getRequestURI().contains(path))) {
      filterChain.doFilter(request, response);
      return;
    }
    final String tokenHeader = request.getHeader(API_BLOG_HEADER);

    if (!apiToken.equals(tokenHeader)) {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
      return;
    }
    filterChain.doFilter(request, response);
  }
}
