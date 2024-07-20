package ag.act.api.admin.stopwords;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.StopWord;
import ag.act.entity.User;
import ag.act.model.CreateStopWordRequest;
import ag.act.model.Status;
import ag.act.model.StopWordDataResponse;
import ag.act.model.StopWordResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.Map;

import static ag.act.enums.ActErrorCode.DUPLICATE_INACTIVE_STOP_WORD_ERROR_CODE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;

class CreateStopWordApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/admin/stop-words";

    private String jwt;

    private CreateStopWordRequest request;

    @BeforeEach
    void setUp() {
        itUtil.init();
        final User adminUser = itUtil.someAdminUser();
        jwt = itUtil.createJwt(adminUser.getId());
    }

    @Nested
    class CreateStopWordSuccessfully {

        @BeforeEach
        void setUp() {
            request = new CreateStopWordRequest().word(someAlphanumericString(20));
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            final MvcResult response = callApi(status().isOk());

            final StopWordDataResponse result = objectMapperUtil.toResponse(
                response.getResponse().getContentAsString(),
                StopWordDataResponse.class
            );

            assertResponse(result);
        }
    }

    @Nested
    class FailToCreateStopWord {
        @DisplayName("로그인 사용자가 어드민이 아닌 경우")
        @Nested
        class WhenNotAdminLoginUser {
            @BeforeEach
            void setUp() {
                jwt = itUtil.createJwt(itUtil.createUser().getId());
                request = new CreateStopWordRequest().word(someAlphanumericString(20));
            }

            @DisplayName("Should return 403 response code when call " + TARGET_API)
            @Test
            void shouldReturnBadRequest() throws Exception {
                final MvcResult response = callApi(status().isForbidden());

                itUtil.assertErrorResponse(response, 403, "인가되지 않은 접근입니다.");
            }
        }

        @DisplayName("이미 해당 금칙어가 존재하는 경우")
        @Nested
        class WhenAlreadyHaveStopWord {

            private StopWord stopWord;

            @BeforeEach
            void setUp() {
                stopWord = itUtil.createStopWord(someAlphanumericString(20));
            }

            @DisplayName("활성화 금칙어인 경우")
            @Nested
            class AndStatusIsActive {
                @BeforeEach
                void setUp() {
                    request = new CreateStopWordRequest().word(stopWord.getWord());
                }

                @DisplayName("Should return 400 response code when call " + TARGET_API)
                @Test
                void shouldReturnBadRequest() throws Exception {
                    final MvcResult response = callApi(status().isBadRequest());

                    itUtil.assertErrorResponse(response, 400, "이미 등록된 금칙어입니다.");
                }
            }

            @DisplayName("비활성화 금칙어인 경우")
            @Nested
            class AndStatusIsInActive {

                private StopWord existingStopWord;

                @BeforeEach
                void setUp() {
                    existingStopWord = itUtil.updateStopWord(stopWord, Status.INACTIVE_BY_ADMIN);
                    request = new CreateStopWordRequest().word(stopWord.getWord());
                }

                @DisplayName("Should return 400 response code when call " + TARGET_API)
                @Test
                void shouldReturnBadRequest() throws Exception {
                    final MvcResult response = callApi(status().isBadRequest());

                    itUtil.assertErrorResponse(
                        response,
                        400,
                        "이미 등록된 비활성화 금칙어입니다. 활성화 하시겠습니까?",
                        DUPLICATE_INACTIVE_STOP_WORD_ERROR_CODE,
                        Map.of("stopWordId", existingStopWord.getId(), "word", existingStopWord.getWord().trim())
                    );
                }
            }

            @DisplayName("단어 양 끝에 공백이 없는 경우")
            @Nested
            class AndWordNotContainsSpace {
                @BeforeEach
                void setUp() {
                    request = new CreateStopWordRequest().word(stopWord.getWord());
                }

                @DisplayName("Should return 400 response code when call " + TARGET_API)
                @Test
                void shouldReturnBadRequest() throws Exception {
                    final MvcResult response = callApi(status().isBadRequest());

                    itUtil.assertErrorResponse(response, 400, "이미 등록된 금칙어입니다.");
                }
            }

            @DisplayName("단어 앞에 공백이 있는 경우")
            @Nested
            class AndWordContainsLeadingWhitespace {
                @BeforeEach
                void setUp() {
                    request = new CreateStopWordRequest().word(" " + stopWord.getWord());
                }

                @DisplayName("Should return 400 response code when call " + TARGET_API)
                @Test
                void shouldReturnBadRequest() throws Exception {
                    final MvcResult response = callApi(status().isBadRequest());

                    itUtil.assertErrorResponse(response, 400, "이미 등록된 금칙어입니다.");
                }
            }

            @DisplayName("단어 끝에 공백이 있는 경우")
            @Nested
            class AndWordContainsTrailingWhitespace {
                @BeforeEach
                void setUp() {
                    request = new CreateStopWordRequest().word(stopWord.getWord() + " ");
                }

                @DisplayName("Should return 400 response code when call " + TARGET_API)
                @Test
                void shouldReturnBadRequest() throws Exception {
                    final MvcResult response = callApi(status().isBadRequest());

                    itUtil.assertErrorResponse(response, 400, "이미 등록된 금칙어입니다.");
                }
            }
        }
    }

    private MvcResult callApi(ResultMatcher resultMatcher) throws Exception {
        return mockMvc
            .perform(
                post(TARGET_API)
                    .content(objectMapperUtil.toJson(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + jwt)
            )
            .andExpect(resultMatcher)
            .andReturn();
    }

    private void assertResponse(StopWordDataResponse result) {
        final StopWordResponse stopWordResponse = result.getData();

        assertThat(stopWordResponse.getWord(), is(request.getWord()));
    }
}
