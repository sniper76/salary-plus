package ag.act.api.admin.apppreference;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.AppPreference;
import ag.act.entity.User;
import ag.act.enums.AppPreferenceType;
import ag.act.model.AppPreferenceResponse;
import ag.act.model.AppPreferenceUpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import static ag.act.TestUtil.assertTime;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomIntegers.someIntegerBetween;

class UpdateAppPreferenceApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/admin/app-preferences/{appPreferenceId}";

    private AppPreference appPreference;
    private AppPreferenceUpdateRequest request;
    private String newValue;
    private String jwt;

    @BeforeEach
    void setUp() {
        itUtil.init();

        User adminUser = itUtil.createAdminUser();
        jwt = itUtil.createJwt(adminUser.getId());
    }

    @Nested
    class WhenNewPostThresholdHours {

        private static final AppPreferenceType type = AppPreferenceType.NEW_POST_THRESHOLD_HOURS;

        @BeforeEach
        void setUp() {
            appPreference = itUtil.findAppPreferenceByType(type);
        }

        @Nested
        class WhenSuccess {

            @BeforeEach
            void setUp() {
                newValue = someIntegerBetween(10, 90).toString();
                request = genRequest(newValue);
            }

            @Test
            void shouldReturnSuccess() throws Exception {
                AppPreferenceResponse response = callApiAndGetResult(status().isOk());

                assertResponseFromDatabase(response);
            }
        }

        @Nested
        class WhenFail {

            @Nested
            class WhenRequestCurrentValueNotEqual {

                @BeforeEach
                void setUp() {
                    newValue = someIntegerBetween(10, 90).toString();
                    request = genRequest(newValue);
                    request = changeCurrentValue(request);
                }

                @Test
                void shouldReturn400Response() throws Exception {
                    callApiAndGetResult(status().isBadRequest());
                }
            }

            @Nested
            class WhenRequestNormal {

                @BeforeEach
                void setUp() {
                    newValue = "invalid";
                    request = genRequest(newValue);
                }

                @Test
                void shouldReturn400Response() throws Exception {
                    callApiAndGetResult(status().isBadRequest());
                }
            }
        }
    }

    @Nested
    class WhenMinAppVersion {

        private static final AppPreferenceType type = AppPreferenceType.MIN_APP_VERSION;

        @BeforeEach
        void setUp() {
            appPreference = itUtil.findAppPreferenceByType(type);
        }

        @Nested
        class WhenSuccess {

            @BeforeEach
            void setUp() {
                newValue = "100.192.0";
                request = genRequest(newValue);
            }

            @Test
            void shouldReturnSuccess() throws Exception {
                AppPreferenceResponse response = callApiAndGetResult(status().isOk());

                assertResponseFromDatabase(response);
            }
        }

        @Nested
        class WhenFail {

            @Nested
            class WhenRequestCurrentValueNotEqual {

                @BeforeEach
                void setUp() {
                    newValue = "100.192.0";
                    request = genRequest(newValue);
                    request = changeCurrentValue(request);
                }

                @Test
                void shouldReturn400Response() throws Exception {
                    callApiAndGetResult(status().isBadRequest());
                }
            }

            @Nested
            class WhenRequestNormal {

                @BeforeEach
                void setUp() {
                    newValue = "invalid";
                    request = genRequest(newValue);
                }

                @Test
                void shouldReturn400Response() throws Exception {
                    callApiAndGetResult(status().isBadRequest());
                }
            }
        }
    }

    @Nested
    class WhenBlockExceptUserIds {

        private static final AppPreferenceType type = AppPreferenceType.BLOCK_EXCEPT_USER_IDS;

        @BeforeEach
        void setUp() {
            appPreference = itUtil.findAppPreferenceByType(type);
        }

        @Nested
        class WhenSuccess {

            @BeforeEach
            void setUp() {
                newValue = "12,34,56";
                request = genRequest(newValue);
            }

            @Test
            void shouldReturnSuccess() throws Exception {
                AppPreferenceResponse response = callApiAndGetResult(status().isOk());

                assertResponseFromDatabase(response);
            }
        }

        @Nested
        class WhenFail {

            @Nested
            class WhenRequestCurrentValueNotEqual {

                @BeforeEach
                void setUp() {
                    newValue = "12,34,56";
                    request = genRequest(newValue);
                    request = changeCurrentValue(request);
                }

                @Test
                void shouldReturn400Response() throws Exception {
                    callApiAndGetResult(status().isBadRequest());
                }
            }

            @Nested
            class WhenRequestNormal {

                @BeforeEach
                void setUp() {
                    newValue = "invalid";
                    request = genRequest(newValue);
                }

                @Test
                void shouldReturn400Response() throws Exception {
                    callApiAndGetResult(status().isBadRequest());
                }
            }
        }
    }

    private void assertResponseFromDatabase(AppPreferenceResponse actual) {
        AppPreference expected = itUtil.findAppPreferenceById(appPreference.getId());

        assertThat(actual.getId(), is(expected.getId()));
        assertThat(actual.getValue(), is(newValue));
        assertThat(actual.getAppPreferenceType(), is(expected.getType().name()));
        assertThat(actual.getCreatedBy(), is(expected.getCreatedBy()));
        assertThat(actual.getUpdatedBy(), is(expected.getUpdatedBy()));
        assertTime(actual.getCreatedAt(), expected.getCreatedAt());
        assertTime(actual.getUpdatedAt(), expected.getUpdatedAt());
    }

    private AppPreferenceUpdateRequest genRequest(String newValue) {
        return new AppPreferenceUpdateRequest()
            .currentValue(appPreference.getValue())
            .newValue(newValue);
    }

    private AppPreferenceResponse callApiAndGetResult(ResultMatcher matcher) throws Exception {
        MvcResult response = mockMvc
            .perform(
                patch(TARGET_API, appPreference.getId())
                    .content(objectMapperUtil.toRequestBody(request))
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

    private AppPreferenceUpdateRequest changeCurrentValue(AppPreferenceUpdateRequest request) {
        final String differentCurrentValue = "different";
        return request.currentValue(differentCurrentValue);
    }
}
