package ag.act.api.admin.apppreference;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.AppPreference;
import ag.act.entity.User;
import ag.act.model.AppPreferenceResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import static ag.act.TestUtil.assertTime;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomLongs.someLongBetween;

class GetAppPreferenceDetailsApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/admin/app-preferences/{appPreferenceId}";

    private Long appPreferenceId;
    private String jwt;
    private Long allAppPreferenceSize;

    @BeforeEach
    void setUp() {
        itUtil.init();
        User admin = itUtil.createAdminUser();
        jwt = itUtil.createJwt(admin.getId());

        allAppPreferenceSize = (long) itUtil.findAllAppPreferences().size();
    }

    @Nested
    class WhenNormal {

        private AppPreference appPreference;

        @BeforeEach
        void setUp() {
            appPreference = itUtil.findAppPreferenceById(someLongBetween(1L, allAppPreferenceSize));
            appPreferenceId = appPreference.getId();
        }

        @Test
        void getAppPreferenceDetails() throws Exception {
            AppPreferenceResponse response = callApiAndGetResult(status().isOk());

            assertResponse(response);
        }

        private void assertResponse(AppPreferenceResponse response) {
            assertThat(response.getId(), is(appPreference.getId()));
            assertThat(response.getAppPreferenceType(), is(appPreference.getType().name()));
            assertThat(response.getValue(), is(appPreference.getValue()));
            assertThat(response.getCreatedBy(), is(appPreference.getCreatedBy()));
            assertThat(response.getUpdatedBy(), is(appPreference.getUpdatedBy()));
            assertTime(response.getCreatedAt(), appPreference.getCreatedAt());
            assertTime(response.getUpdatedAt(), appPreference.getUpdatedAt());
        }
    }

    @Nested
    class WhenNotExist {

        @BeforeEach
        void setUp() {
            appPreferenceId = someLongBetween(allAppPreferenceSize, Long.MAX_VALUE);
        }

        @Test
        void getAppPreferenceDetails() throws Exception {
            callApiAndGetResult(status().isBadRequest());
        }
    }

    private AppPreferenceResponse callApiAndGetResult(ResultMatcher matcher) throws Exception {
        MvcResult response = mockMvc
            .perform(
                get(TARGET_API, appPreferenceId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + jwt)
            )
            .andExpect(matcher)
            .andReturn();

        return objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            AppPreferenceResponse.class
        );
    }
}
