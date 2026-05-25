package br.com.challenge.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

@AutoConfigureMockMvc
@SpringBootTest(
    properties = {
      "spring.profiles.active=local",
      "management.prometheus.metrics.export.enabled=false",
      "spring.security.user.password=challenge"
    })
@Import(TestContainerConfig.class)
public abstract class BaseTest {

  protected static final String AUTH_HEADER = "API-BLOG";
  protected static final String VALID_TOKEN = "LKUBv5oNJ8D2hs3LMy0yemrkeEu6bm4SUGryjjr/5o8=";

  @Autowired protected MockMvc mockMvc;
  @Autowired protected JsonMapper jsonMapper;
}
