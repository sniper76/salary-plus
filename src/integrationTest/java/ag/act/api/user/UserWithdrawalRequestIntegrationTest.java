package ag.act.api.user;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Board;
import ag.act.entity.Post;
import ag.act.entity.Solidarity;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.entity.solidarity.election.SolidarityLeaderElection;
import ag.act.enums.DigitalDocumentType;
import ag.act.enums.solidarity.election.SolidarityLeaderElectionApplyStatus;
import ag.act.model.SimpleStringResponse;
import ag.act.model.Status;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.verification.VerificationMode;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.net.URI;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomStrings.someString;

class UserWithdrawalRequestIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/users/withdrawal";

    @Mock
    private HttpResponse<String> postResponse;
    @Mock
    private HttpResponse<String> deleteResponse;

    private String jwt;
    private User user;
    private Long userId;

    @BeforeEach
    void setUp() throws Exception {
        itUtil.init();
        user = itUtil.createUser();
        userId = user.getId();
        jwt = itUtil.createJwt(userId);

        // 외부서비스는 Mocking 한다.
        mockWithdrawMyDataService();
        mockGetFinpongAccessTokenFromMyDataService();
    }

    @Nested
    class WhenNormalUser {

        @BeforeEach
        void setUp() {
            itUtil.createMyDataSummary(user);
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            final MvcResult response = callApi(status().isOk());
            final SimpleStringResponse result = getResult(response);

            assertResponse(result);

            assertMyDataGetFinpongToken(times(1));
            assertMyDataDeleteApiCall(times(1));
        }
    }

    @Nested
    class WhenUserIsLeader {

        @BeforeEach
        void setUp() {
            Stock stock = itUtil.createStock();
            final Solidarity solidarity = itUtil.createSolidarity(stock.getCode());
            itUtil.createUserHoldingStock(stock.getCode(), user);
            itUtil.createSolidarityLeader(solidarity, user.getId());
            itUtil.createMyDataSummary(user);
        }

        @DisplayName("Should return 400 response code when call " + TARGET_API)
        @Test
        void shouldReturnBadRequest() throws Exception {
            final MvcResult response = callApi(status().isBadRequest());

            itUtil.assertErrorResponse(response, BAD_REQUEST_STATUS, "주주대표는 탈퇴할 수 없습니다. 관리자에게 문의해주세요.");
            assertUserIsStillActive(itUtil.findUser(user.getId()));
        }

    }

    @Nested
    class WhenUserIsAcceptor {
        private Stock stock;

        @BeforeEach
        void setUp() {
            stock = itUtil.createStock();
            itUtil.createSolidarity(stock.getCode());
            itUtil.createUserHoldingStock(stock.getCode(), user);
            itUtil.createStockAcceptorUser(stock.getCode(), user);
            itUtil.createMyDataSummary(user);
        }

        @Nested
        class WhenNotFoundDigitalDocument {

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                final MvcResult response = callApi(status().isOk());
                final SimpleStringResponse result = getResult(response);

                assertResponse(result);
            }
        }

        @Nested
        class WhenHasProcessingDigitalDocument {

            @BeforeEach
            void setUp() {
                final Board board = itUtil.createBoard(stock);
                final User adminUser = itUtil.createAdminUser();
                final Post post = itUtil.createPost(board, adminUser.getId(), Boolean.FALSE);
                final LocalDateTime now = LocalDateTime.now();
                final LocalDateTime targetStartDate = now.minusDays(5);
                final LocalDateTime targetEndDate = now.plusDays(1);
                final LocalDate referenceDate = now.toLocalDate();
                itUtil.createDigitalDocument(
                    post, stock, user, DigitalDocumentType.DIGITAL_PROXY, targetStartDate, targetEndDate, referenceDate
                );
            }

            @DisplayName("Should return 400 response code when call " + TARGET_API)
            @Test
            void shouldReturnBadRequest() throws Exception {
                final MvcResult response = callApi(status().isBadRequest());

                itUtil.assertErrorResponse(response, BAD_REQUEST_STATUS, "수임인으로 진행중인 전자문서가 존재하여 탈퇴할 수 없습니다. 관리자에게 문의해주세요.");
                assertResponseFromDatabase(itUtil.findUser(user.getId()));
            }
        }

        private void assertResponseFromDatabase(User userFromDatabase) {
            assertThat(userFromDatabase.getStatus(), is(Status.ACTIVE));
        }

    }

    @DisplayName("유저가 마이데이터를 연동하지 않은 경우")
    @Nested
    class WhenUserDoesNotHaveMyData {

        @DisplayName("탈퇴 요청 성공")
        @Test
        void shouldReturnSuccess() throws Exception {
            final MvcResult response = callApi(status().isOk());
            final SimpleStringResponse result = getResult(response);

            assertResponse(result);

            assertMyDataGetFinpongToken(never());
            assertMyDataDeleteApiCall(never());
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
            }

            @DisplayName("탈퇴 요청 불가능")
            @Test
            void shouldReturnBadRequest() throws Exception {
                final MvcResult response = callApi(status().isBadRequest());

                itUtil.assertErrorResponse(response, BAD_REQUEST_STATUS, "주주대표 선출 진행 중에는 회원탈퇴를 할 수 없습니다.");
                assertUserIsStillActive(itUtil.findUser(user.getId()));
            }
        }

        @DisplayName("그리고 지원서를 임시 저장한 경우")
        @Nested
        class AndApplicationIsSaved {

            @BeforeEach
            void setUp() {
                applyForSolidarityLeaderElectionAndSavedApplication();
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                final MvcResult response = callApi(status().isOk());
                final SimpleStringResponse result = getResult(response);

                assertResponse(result);
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

    private void assertUserIsStillActive(User userFromDatabase) {
        assertThat(userFromDatabase.getStatus(), is(Status.ACTIVE));
    }

    private SimpleStringResponse getResult(MvcResult response) throws Exception {
        return itUtil.getResult(response, SimpleStringResponse.class);
    }

    private void mockGetFinpongAccessTokenFromMyDataService() throws Exception {
        final String responseBody = genResponseForFinpongAccessToken();
        given(defaultHttpClientUtil.post(any(), anyLong(), anyString())).willReturn(postResponse);
        given(postResponse.body()).willReturn(responseBody);
    }

    private String genResponseForFinpongAccessToken() {
        Map<String, String> result = Map.of("code", "FP-00000");
        Map<String, String> data = Map.of("accessToken", someString(50));
        Map<String, Map<String, String>> response = Map.of("result", result, "data", data);
        return objectMapperUtil.toJson(response);
    }

    private void mockWithdrawMyDataService() throws Exception {
        final String responseBody = genResponse();
        given(defaultHttpClientUtil.delete(any(URI.class), anyMap())).willReturn(deleteResponse);
        given(deleteResponse.body()).willReturn(responseBody);
    }

    private String genResponse() {
        Map<String, String> result = Map.of("code", "FP-00000");
        Map<String, String> data = Map.of("accessToken", someString(50));
        Map<String, Map<String, String>> response = Map.of("result", result, "data", data);
        return objectMapperUtil.toJson(response);
    }

    private void assertResponse(SimpleStringResponse result) {
        assertThat(result.getStatus(), is("ok"));

        final User userFromDatabase = itUtil.findUser(user.getId());
        assertThat(userFromDatabase.getStatus(), is(Status.WITHDRAWAL_REQUESTED));
        assertThat(userFromDatabase.getLastPinNumberVerifiedAt(), nullValue());
    }

    private void assertMyDataDeleteApiCall(VerificationMode verificationMode) throws Exception {
        then(defaultHttpClientUtil).should(verificationMode).delete(any(URI.class), anyMap());
    }

    private void assertMyDataGetFinpongToken(VerificationMode verificationMode) throws Exception {
        then(defaultHttpClientUtil).should(verificationMode).post(any(), anyLong(), anyString());
    }

    @NotNull
    private MvcResult callApi(ResultMatcher resultMatcher) throws Exception {
        return mockMvc
            .perform(
                post(TARGET_API)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers(jwt(jwt)))
            )
            .andExpect(resultMatcher)
            .andReturn();
    }
}
