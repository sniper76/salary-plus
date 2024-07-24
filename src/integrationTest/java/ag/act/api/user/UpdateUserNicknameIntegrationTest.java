package ag.act.api.user;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Solidarity;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.entity.solidarity.election.SolidarityLeaderElection;
import ag.act.enums.solidarity.election.SolidarityLeaderElectionApplyStatus;
import ag.act.model.SimpleStringResponse;
import ag.act.model.UpdateMyNicknameRequest;
import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.time.LocalDateTime;

import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomIntegers.someIntegerBetween;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;
import static shiver.me.timbers.data.random.RandomStrings.someString;

class UpdateUserNicknameIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/users/nickname";

    private UpdateMyNicknameRequest request;
    private String nickname;
    private User user;
    private Long userId;
    private String jwt;

    @BeforeEach
    void setUp() {
        itUtil.init();

        user = itUtil.createUser();
        userId = user.getId();
        jwt = itUtil.createJwt(userId);
    }

    @Nested
    class WhenUpdateNicknameSuccess {

        @BeforeEach
        void setUp() {
            request = genRequest();
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            final MvcResult response = callApi(status().isOk());

            final SimpleStringResponse result = objectMapperUtil.toResponse(
                response.getResponse().getContentAsString(),
                SimpleStringResponse.class
            );

            assertResponse(result);
            assertUserFromDatabase();
        }
    }

    @Nested
    class WhenNicknameContainsStopWord {

        @BeforeEach
        void setUp() {
            nickname = someAlphanumericString(30);
            request = genRequest(nickname);

            final String stopWord = nickname.substring(3, 20);
            itUtil.createStopWord(stopWord);
        }

        @DisplayName("Should return 400 response code when call " + TARGET_API)
        @Test
        void shouldReturnBadRequest() throws Exception {
            final MvcResult response = callApi(status().is(BAD_REQUEST_STATUS));

            itUtil.assertErrorResponse(response, BAD_REQUEST_STATUS, "닉네임에 사용할 수 없는 단어입니다.");
        }
    }

    @Nested
    class WhenNicknameAlreadyUsedByOtherUser {
        @BeforeEach
        void setUp() {
            request = genRequest();
            createAnotherUserWhoUseTheSameNickname();
        }

        @DisplayName("Should return 400 response code when call " + TARGET_API)
        @Test
        void shouldReturnBadRequest() throws Exception {
            final MvcResult response = callApi(status().is(BAD_REQUEST_STATUS));

            itUtil.assertErrorResponse(response, BAD_REQUEST_STATUS, "이미 사용중인 닉네임입니다.");
        }
    }

    @Nested
    class WhenEmptyNicknameRequest {
        @BeforeEach
        void setUp() {
            request = genEmptyRequest();
        }

        @DisplayName("Should return 400 response code when call " + TARGET_API)
        @Test
        void shouldReturnBadRequest() throws Exception {
            final MvcResult response = callApi(status().is(BAD_REQUEST_STATUS));

            itUtil.assertErrorResponse(response, BAD_REQUEST_STATUS, "닉네임을 확인해주세요.");
        }
    }

    @Nested
    class WhenModificationRestrictionPeriodPassed {
        @BeforeEach
        void setUp() {
            itUtil.updateUserNickname(user, someString(10), false);
            itUtil.updateAllNicknameHistoriesCreatedAtWithinTimeRange(user, someIntegerBetween(91, 100));
            user = itUtil.findUser(userId);
            request = genRequest();
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldSuccess() throws Exception {
            final MvcResult response = callApi(status().isOk());

            final SimpleStringResponse result = objectMapperUtil.toResponse(
                response.getResponse().getContentAsString(),
                SimpleStringResponse.class
            );

            assertResponse(result);
            assertUserFromDatabase();
        }
    }

    @Nested
    class WhenModificationRestrictionPeriodNotPassed {
        @BeforeEach
        void setUp() {
            itUtil.updateUserNickname(user, someString(10), false);
            request = genRequest();
        }

        @DisplayName("Should return 400 response code when call " + TARGET_API)
        @Test
        void shouldReturnBadRequest() throws Exception {
            final MvcResult response = callApi(status().isBadRequest());

            itUtil.assertErrorResponse(response, BAD_REQUEST_STATUS, "닉네임은 90일에 한번만 변경할 수 있습니다.");
        }
    }

    @Nested
    class WhenModificationRestrictionPeriodPassedAndAdminChangedJustBefore {
        @BeforeEach
        void setUp() {
            itUtil.updateUserNickname(user, someString(10), false);
            itUtil.updateAllNicknameHistoriesCreatedAtWithinTimeRange(user, someIntegerBetween(91, 100));
            user = itUtil.findUser(userId);
            itUtil.updateUserNickname(user, someString(10), true);
            request = genRequest();
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldSuccess() throws Exception {
            final MvcResult response = callApi(status().isOk());

            final SimpleStringResponse result = objectMapperUtil.toResponse(
                response.getResponse().getContentAsString(),
                SimpleStringResponse.class
            );

            assertResponse(result);
            assertUserFromDatabase();
        }
    }

    @Nested
    class WhenModificationRestrictionPeriodNotPassedAndAdminChangedJustBefore {
        @BeforeEach
        void setUp() {
            itUtil.updateUserNickname(user, someString(10), false);
            itUtil.updateAllNicknameHistoriesCreatedAtWithinTimeRange(user, someIntegerBetween(10, 90));
            user = itUtil.findUser(userId);
            itUtil.updateUserNickname(user, someString(10), true);

            request = genRequest();
        }

        @DisplayName("Should return 400 response code when call " + TARGET_API)
        @Test
        void shouldReturnBadRequest() throws Exception {
            final MvcResult response = callApi(status().is(BAD_REQUEST_STATUS));

            itUtil.assertErrorResponse(response, BAD_REQUEST_STATUS, "닉네임은 90일에 한번만 변경할 수 있습니다.");
        }
    }

    @DisplayName("회원이 현재 진행중인 주주대표 선출투표에 지원한 후보자인 경우")
    @Nested
    class WhenUserAppliedForInProgressSolidarityLeaderElection {

        @DisplayName("그리고 지원서를 완료한 경우")
        @Nested
        class AndApplicationIsCompleted {

            @BeforeEach
            void setUp() {
                applyForSolidarityLeaderElectionAndCompletedApplication();

                request = genRequest();
            }

            @DisplayName("Should return 400 response code when call " + TARGET_API)
            @Test
            void shouldReturnBadRequest() throws Exception {
                final MvcResult response = callApi(status().is(BAD_REQUEST_STATUS));

                itUtil.assertErrorResponse(
                    response,
                    BAD_REQUEST_STATUS,
                    "주주대표 지원자는 대표 선출 기간 동안 주주들의 혼선을 방지하기 위하여 닉네임 변경이 불가합니다."
                );
            }
        }

        @DisplayName("그리고 지원서를 임시 저장한 경우")
        @Nested
        class AndApplicationIsSaved {

            @BeforeEach
            void setUp() {
                applyForSolidarityLeaderElectionAndSavedApplication();

                request = genRequest();
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                final MvcResult response = callApi(status().isOk());

                final SimpleStringResponse result = objectMapperUtil.toResponse(
                    response.getResponse().getContentAsString(),
                    SimpleStringResponse.class
                );

                assertResponse(result);
                assertUserFromDatabase();
            }
        }

        private void applyForSolidarityLeaderElectionAndCompletedApplication() {
            applyForSolidarityLeaderElectionWithApplyStatus(SolidarityLeaderElectionApplyStatus.COMPLETE);
        }

        private void applyForSolidarityLeaderElectionAndSavedApplication() {
            applyForSolidarityLeaderElectionWithApplyStatus(SolidarityLeaderElectionApplyStatus.SAVE);
        }

        private void applyForSolidarityLeaderElectionWithApplyStatus(SolidarityLeaderElectionApplyStatus applyStatus) {
            final Stock stock = itUtil.createStock();
            final Solidarity solidarity = itUtil.createSolidarity(stock.getCode());
            final SolidarityLeaderElection solidarityElection = itUtil.createSolidarityElection(stock.getCode(), LocalDateTime.now());
            itUtil.createSolidarityLeaderApplicant(
                solidarity.getId(),
                userId,
                applyStatus,
                solidarityElection.getId()
            );
        }
    }

    private MvcResult callApi(ResultMatcher matcher) throws Exception {
        return mockMvc
            .perform(
                patch(TARGET_API)
                    .content(objectMapperUtil.toRequestBody(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers(jwt(jwt)))
            )
            .andExpect(matcher)
            .andReturn();
    }

    private void createAnotherUserWhoUseTheSameNickname() {
        final User anotherUser = itUtil.createUser();
        anotherUser.setNickname(nickname); // set the same nickname.
        itUtil.updateUser(anotherUser);
    }

    private UpdateMyNicknameRequest genRequest() {

        nickname = someAlphanumericString(20);

        UpdateMyNicknameRequest request = new UpdateMyNicknameRequest();
        request.setNickname(nickname);

        return request;
    }

    private UpdateMyNicknameRequest genRequest(String nickname) {

        UpdateMyNicknameRequest request = new UpdateMyNicknameRequest();
        request.setNickname(nickname);

        return request;
    }

    private UpdateMyNicknameRequest genEmptyRequest() {
        nickname = Strings.EMPTY;

        UpdateMyNicknameRequest request = new UpdateMyNicknameRequest();
        request.setNickname(nickname);

        return request;
    }

    private void assertResponse(SimpleStringResponse result) {
        assertThat(result.getStatus(), is("ok"));
    }

    private void assertUserFromDatabase() {
        final User user = itUtil.findUser(userId);
        assertThat(user.getNickname(), is(nickname));
    }
}
