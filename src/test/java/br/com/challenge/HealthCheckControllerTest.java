package br.com.challenge;

import br.com.challenge.config.BaseTest;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

class HealthCheckControllerTest extends BaseTest {

  @Test
  void testActuatorUrlDoesNotExist() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/actuator/invalid/url").header(AUTH_HEADER, VALID_TOKEN))
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  void testIsHealthCheckAvailable() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/health/check").header(AUTH_HEADER, VALID_TOKEN))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }
}
