package ag.act.api.admin.stopwords;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.StopWord;
import ag.act.entity.User;
import ag.act.model.Status;
import ag.act.model.StopWordDataResponse;
import ag.act.model.StopWordResponse;
import ag.act.model.UpdateStopWordRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import static ag.act.TestUtil.assertTime;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomLongs.someLong;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;

class UpdateStopWordApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/admin/stop-words/{stopWordId}";

    private String jwt;

    private UpdateStopWordRequest request;
    private StopWord stopWord;
    private Long stopWordId;

    @BeforeEach
    void setUp() {
        itUtil.init();
        final User adminUser = itUtil.someAdminUser();
        jwt = itUtil.createJwt(adminUser.getId());
    }

    @Nested
    class UpdateStopWordSuccessfully {

        @Nested
        @DisplayName("금칙어를 비활성화 하는 경우")
        class WhenUpdateStatusToInactive {

            @BeforeEach
            void setUp() {
                stopWord = itUtil.createStopWord(someAlphanumericString(20), Status.ACTIVE);
                stopWordId = stopWord.getId();
                request = new UpdateStopWordRequest()
                    .beforeStatus(Status.ACTIVE.name())
                    .afterStatus(Status.INACTIVE_BY_ADMIN.name());
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
        @DisplayName("금칙어를 활성화 하는 경우")
        class WhenUpdateStatusToActive {
            @BeforeEach
            void setUp() {
                stopWord = itUtil.createStopWord(someAlphanumericString(20), Status.INACTIVE_BY_ADMIN);
                stopWordId = stopWord.getId();
                request = new UpdateStopWordRequest()
                    .beforeStatus(Status.INACTIVE_BY_ADMIN.name())
                    .afterStatus(Status.ACTIVE.name());
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
    }

    @Nested
    class FailToUpdateStopWord {
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
                request = new UpdateStopWordRequest()
                    .beforeStatus(Status.ACTIVE.name())
                    .afterStatus(Status.INACTIVE_BY_ADMIN.name());
            }

            @DisplayName("Should return 404 response code when call " + TARGET_API)
            @Test
            void shouldReturnBadRequest() throws Exception {
                final MvcResult response = callApi(status().isNotFound());

                itUtil.assertErrorResponse(response, 404, "존재하지 않는 금칙어입니다.");
            }
        }

        @DisplayName("금칙어 상태가 아닌 경우")
        @Nested
        class WhenNotSupportedStatus {
            private String status;

            @BeforeEach
            void setUp() {
                stopWord = itUtil.createStopWord(someAlphanumericString(20));
                stopWordId = stopWord.getId();
            }

            @DisplayName("지원하지 않는 변경 이전 상태인 경우")
            @Nested
            class AndBeforeStatusNotSupported {
                @BeforeEach
                void setUp() {
                    status = someAlphanumericString(1, 20).toUpperCase();
                    request = new UpdateStopWordRequest()
                        .beforeStatus(status)
                        .afterStatus(Status.INACTIVE_BY_ADMIN.name());
                }

                @DisplayName("Should return 400 response code when call " + TARGET_API)
                @Test
                void shouldReturnBadRequest() throws Exception {
                    final MvcResult response = callApi(status().isBadRequest());

                    itUtil.assertErrorResponse(response, 400, "Unexpected value '" + status + "'");
                }
            }

            @DisplayName("지원하지 않는 변경 이후 상태인 경우")
            @Nested
            class AndAfterStatusNotSupported {
                @BeforeEach
                void setUp() {
                    status = someAlphanumericString(1, 20).toUpperCase();
                    request = new UpdateStopWordRequest()
                        .beforeStatus(Status.ACTIVE.name())
                        .afterStatus(status);
                }

                @DisplayName("Should return 400 response code when call " + TARGET_API)
                @Test
                void shouldReturnBadRequest() throws Exception {
                    final MvcResult response = callApi(status().isBadRequest());

                    itUtil.assertErrorResponse(response, 400, "Unexpected value '" + status + "'");
                }
            }

            @Nested
            @DisplayName("상태가 금칙어 상태가 아닌 경우")
            class AndNotStopWordStatus {
                @BeforeEach
                void setUp() {
                    request = new UpdateStopWordRequest()
                        .beforeStatus(Status.ACTIVE.name())
                        .afterStatus(Status.INACTIVE_BY_USER.name());
                }

                @DisplayName("Should return 400 response code when call " + TARGET_API)
                @Test
                void shouldReturnBadRequest() throws Exception {
                    final MvcResult response = callApi(status().isBadRequest());

                    itUtil.assertErrorResponse(response, 400, "상태를 확인해주세요.");
                }
            }
        }

        @DisplayName("이미 상태를 업데이트 한 경우")
        @Nested
        class WhenAlreadyUpdated {

            @DisplayName("이미 비활성화된 금칙어를 비활성화 하는 경우")
            @Nested
            class AlreadyInActive {
                @BeforeEach
                void setUp() {
                    stopWord = itUtil.createStopWord(someAlphanumericString(20), Status.INACTIVE_BY_ADMIN);
                    stopWordId = stopWord.getId();
                    request = new UpdateStopWordRequest()
                        .beforeStatus(Status.ACTIVE.name())
                        .afterStatus(Status.INACTIVE_BY_ADMIN.name());
                }

                @DisplayName("Should return 400 response code when call " + TARGET_API)
                @Test
                void shouldReturnBadRequest() throws Exception {
                    final MvcResult response = callApi(status().isBadRequest());

                    itUtil.assertErrorResponse(response, 400, "이미 비활성화된 금칙어입니다.");
                }
            }

            @DisplayName("이미 활성화된 금칙어를 활성화 하는 경우")
            @Nested
            class AlreadyActive {
                @BeforeEach
                void setUp() {
                    stopWord = itUtil.createStopWord(someAlphanumericString(20), Status.ACTIVE);
                    stopWordId = stopWord.getId();
                    request = new UpdateStopWordRequest()
                        .beforeStatus(Status.INACTIVE_BY_ADMIN.name())
                        .afterStatus(Status.ACTIVE.name());
                }

                @DisplayName("Should return 400 response code when call " + TARGET_API)
                @Test
                void shouldReturnBadRequest() throws Exception {
                    final MvcResult response = callApi(status().isBadRequest());

                    itUtil.assertErrorResponse(response, 400, "이미 활성화된 금칙어입니다.");
                }
            }
        }
    }

    private MvcResult callApi(ResultMatcher resultMatcher) throws Exception {
        return mockMvc
            .perform(
                post(TARGET_API, stopWordId)
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

        assertThat(stopWordResponse.getId(), is(stopWord.getId()));
        assertThat(stopWordResponse.getWord(), is(stopWord.getWord()));
        assertThat(stopWordResponse.getStatus().name(), is(request.getAfterStatus()));
        assertTime(stopWordResponse.getCreatedAt(), stopWord.getCreatedAt());
        assertTime(stopWordResponse.getCreatedAt(), stopWord.getUpdatedAt());
    }
}
