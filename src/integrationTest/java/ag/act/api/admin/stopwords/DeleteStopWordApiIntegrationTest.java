package ag.act.api.admin.stopwords;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.StopWord;
import ag.act.entity.User;
import ag.act.model.SimpleStringResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomLongs.someLong;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;

class DeleteStopWordApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/admin/stop-words/{stopWordId}";

    private String jwt;

    private Long stopWordId;

    @BeforeEach
    void setUp() {
        itUtil.init();
        final User adminUser = itUtil.someAdminUser();
        jwt = itUtil.createJwt(adminUser.getId());

        final StopWord stopWord = itUtil.createStopWord(someAlphanumericString(20));
        stopWordId = stopWord.getId();
    }

    @Nested
    class DeleteStopWordSuccessfully {
        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            final MvcResult response = callApi(status().isOk());

            final SimpleStringResponse result = objectMapperUtil.toResponse(
                response.getResponse().getContentAsString(),
                SimpleStringResponse.class
            );

            assertResponse(result);
        }
    }

    @Nested
    class FailToDeleteStopWord {
        @DisplayName("로그인 사용자가 어드민이 아닌 경우")
        @Nested
        class WhenNotAdminLoginUser {
            @BeforeEach
            void setUp() {
                jwt = itUtil.createJwt(itUtil.createUser().getId());
            }

            @DisplayName("Should return 403 response code when call " + TARGET_API)
            @Test
            void shouldReturnBadRequest() throws Exception {
                final MvcResult response = callApi(status().isForbidden());

                itUtil.assertErrorResponse(response, 403, "인가되지 않은 접근입니다.");
            }
        }

        @DisplayName("금칙어가 존재하지 않는 경우")
        @Nested
        class WhenStopWordNotFound {

            @BeforeEach
            void setUp() {
                stopWordId = someLong();
            }

            @DisplayName("Should return 404 response code when call " + TARGET_API)
            @Test
            void shouldReturnBadRequest() throws Exception {
                final MvcResult response = callApi(status().isNotFound());

                itUtil.assertErrorResponse(response, 404, "존재하지 않는 금칙어입니다.");
            }
        }
    }

    private MvcResult callApi(ResultMatcher resultMatcher) throws Exception {
        return mockMvc
            .perform(
                delete(TARGET_API, stopWordId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + jwt)
            )
            .andExpect(resultMatcher)
            .andReturn();
    }

    private void assertResponse(SimpleStringResponse result) {
        assertThat(result.getStatus(), is("ok"));
    }

}
