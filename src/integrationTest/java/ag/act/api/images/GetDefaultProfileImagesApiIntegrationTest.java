package ag.act.api.images;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.User;
import ag.act.model.DefaultProfileImageResponse;
import jakarta.validation.Valid;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GetDefaultProfileImagesApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/images/default-profile-images";

    private String jwt;

    @BeforeEach
    void setUp() {
        itUtil.init();
        User user = itUtil.createUser();
        jwt = itUtil.createJwt(user.getId());
    }

    @Nested
    class GetDefaultProfileImages {

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            MvcResult response = mockMvc
                .perform(
                    get(TARGET_API)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .headers(headers(jwt(jwt)))
                )
                .andExpect(status().isOk())
                .andReturn();

            final ag.act.model.DefaultProfileImagesResponse result = objectMapperUtil.toResponse(
                response.getResponse().getContentAsString(),
                ag.act.model.DefaultProfileImagesResponse.class
            );

            assertResponse(result);
        }

        private void assertResponse(ag.act.model.DefaultProfileImagesResponse result) {
            final List<@Valid DefaultProfileImageResponse> defaultProfileImageResponses = result.getData();

            assertThat(defaultProfileImageResponses.size(), is(2));
            assertThat(defaultProfileImageResponses.get(0).getId(), notNullValue());
            assertThat(defaultProfileImageResponses.get(0).getUrl(), notNullValue());
            assertThat(defaultProfileImageResponses.get(0).getGender(), is("M"));
            assertThat(defaultProfileImageResponses.get(1).getId(), notNullValue());
            assertThat(defaultProfileImageResponses.get(1).getUrl(), notNullValue());
            assertThat(defaultProfileImageResponses.get(1).getGender(), is("F"));
        }

    }
}
