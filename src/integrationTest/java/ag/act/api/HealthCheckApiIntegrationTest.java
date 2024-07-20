package ag.act.api;

import ag.act.AbstractCommonIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class HealthCheckApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String HEALTH_API_PATH = "/api/health";

    @DisplayName("Should return 200 response code with OK when health endpoint called")
    @Test
    void shouldReturnSuccessfulResponseWhenHealthCheckCalled() throws Exception {
        MvcResult response = mockMvc.perform(get(HEALTH_API_PATH)).andExpect(status().isOk()).andReturn();

        assertThat(response.getResponse().getContentAsString()).isEqualTo("{\"status\":\"ok\"}");
    }
}
