package ag.act.api.stocksolidarity.leaderelection;

import ag.act.dto.solidarity.election.SolidarityLeaderElectionStatusGroup;
import ag.act.entity.SolidarityDailySummary;
import ag.act.entity.SolidarityLeaderApplicant;
import ag.act.entity.User;
import ag.act.entity.solidarity.election.LeaderElectionCurrentDateTimeProvider;
import ag.act.entity.solidarity.election.SolidarityLeaderElection;
import ag.act.enums.SolidarityLeaderElectionProcedure;
import ag.act.enums.solidarity.election.SolidarityLeaderElectionApplyStatus;
import ag.act.enums.solidarity.election.SolidarityLeaderElectionStatus;
import ag.act.enums.solidarity.election.SolidarityLeaderElectionStatusDetails;
import ag.act.util.KoreanDateTimeUtil;
import ag.act.util.ZoneIdUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static ag.act.TestUtil.someSolidarityLeaderElectionApplicationItem;
import static ag.act.TestUtil.someSolidarityLeaderElectionApplyStatus;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomDoubles.someDoubleBetween;
import static shiver.me.timbers.data.random.RandomIntegers.someIntegerBetween;
import static shiver.me.timbers.data.random.RandomStrings.someString;

class CreateSolidarityLeaderApplicantApiIntegrationTest extends AbstractSolidarityLeaderElectionApiIntegrationTest {

    private static final String TARGET_API = "/api/stocks/{stockCode}/solidarity-leader-elections/solidarity-leader-applicants";

    @Nested
    class WhenSuccess {
        private LocalDateTime currentKoreanDateTime;
        private LocalDateTime expectedCandidateRegistrationStartDateTime;
        private List<MockedStatic<?>> statics;

        @AfterEach
        void tearDown() {
            statics.forEach(MockedStatic::close);
        }

        @BeforeEach
        void setUp() {
            statics = List.of(mockStatic(LeaderElectionCurrentDateTimeProvider.class));
            final ZonedDateTime currentZonedDateTime = ZonedDateTime.now(ZoneIdUtil.getSeoulZoneId())
                .withNano(0);
            currentKoreanDateTime = currentZonedDateTime.toLocalDateTime();
            expectedCandidateRegistrationStartDateTime = currentZonedDateTime.toLocalDateTime().minusHours(KOREAN_TIME_OFFSET);

            given(LeaderElectionCurrentDateTimeProvider.get())
                .willReturn(expectedCandidateRegistrationStartDateTime);
            given(LeaderElectionCurrentDateTimeProvider.getKoreanDateTime())
                .willReturn(currentZonedDateTime);
        }


        @Nested
        class WhenStatusSave {

            @Nested
            class WhenFirstUserSave {

                @BeforeEach
                void setUp() {
                    request = genRequest(someString(0, 50), SolidarityLeaderElectionApplyStatus.SAVE);
                }

                @Test
                @DisplayName("지원서를 임시 저장할 수 있다.")
                void shouldReturnSuccess() throws Exception {
                    MvcResult mvcResult = callApi(status().isOk());

                    itUtil.assertSimpleOkay(mvcResult);
                    assertSolidarityLeaderElectionIsPending();
                    assertNotSendSlackMessage();
                }

                @Nested
                class WhenBothUserSaved {

                    private SolidarityLeaderElection election;

                    @BeforeEach
                    void setUp() {
                        election = itUtil.createSolidarityElection(
                            stock.getCode(),
                            SolidarityLeaderElectionStatusGroup.CANDIDATE_REGISTER_PENDING_STATUS_GROUP
                        );

                        User anotherUser = itUtil.createUser();
                        itUtil.createSolidarityLeaderApplicant(
                            solidarity.getId(),
                            anotherUser.getId(),
                            SolidarityLeaderElectionApplyStatus.SAVE,
                            election.getId()
                        );

                        request = genRequest(someSolidarityLeaderElectionApplicationItem(), SolidarityLeaderElectionApplyStatus.SAVE);
                    }

                    @Test
                    @DisplayName("두명의 사용자가 모두 임시저장하는 경우에도 하나의 선출만 생성한다.")
                    void shouldNotCreateElection() throws Exception {
                        MvcResult mvcResult = callApi(status().isOk());

                        itUtil.assertSimpleOkay(mvcResult);
                        assertSolidarityLeaderElectionIsPending();
                        assertSolidarityLeaderApplicant();
                    }

                    private void assertSolidarityLeaderApplicant() {
                        Optional<SolidarityLeaderApplicant> solidarityLeaderApplicant =
                            itUtil.findSolidarityLeaderApplicant(solidarity.getId(), user.getId());
                        assertThat(solidarityLeaderApplicant.isPresent(), is(true));
                        assertThat(solidarityLeaderApplicant.get().getSolidarityLeaderElectionId(), is(election.getId()));
                    }
                }

            }
        }

        @Nested
        class WhenStatusComplete {

            @BeforeEach
            void setUp() {
                candidateCount = 1;
                request = genRequest(someSolidarityLeaderElectionApplicationItem(), SolidarityLeaderElectionApplyStatus.COMPLETE);
            }

            @Test
            @DisplayName("후보자가 지원서를 완료하면 주주대표 선출 투표가 생성된다.")
            void shouldReturnSuccess() throws Exception {
                MvcResult mvcResult = callApi(status().isOk());

                final Optional<SolidarityLeaderElection> electionOptional = itUtil.findSolidarityLeaderElectionByStockCode(stock.getCode());
                assertThat(electionOptional.isPresent(), is(true));

                itUtil.assertSimpleOkay(mvcResult);
                assertSolidarityLeaderElection(electionOptional.get(), currentKoreanDateTime, expectedCandidateRegistrationStartDateTime);
                assertSendSlackMessage();
            }
        }

        @Nested
        @DisplayName("진행중인 선출에 삭제한 지원서가 있을 때")
        class WhenStatusDeletedByUser {

            @BeforeEach
            void setUp() {
                SolidarityLeaderElection election = itUtil.createSolidarityElection(
                    stock.getCode(),
                    SolidarityLeaderElectionStatusGroup.CANDIDATE_REGISTER_STATUS_GROUP,
                    expectedCandidateRegistrationStartDateTime
                );

                User anotherUser = itUtil.createUser();
                itUtil.createSolidarityLeaderApplicant(
                    solidarity.getId(),
                    anotherUser.getId(),
                    SolidarityLeaderElectionApplyStatus.COMPLETE,
                    election.getId()
                );

                // user apply and delete
                itUtil.createSolidarityLeaderApplicant(
                    solidarity.getId(),
                    user.getId(),
                    SolidarityLeaderElectionApplyStatus.DELETED_BY_USER,
                    election.getId()
                );

                election.setCandidateCount(1);
                itUtil.updateSolidarityElection(election);

                candidateCount = 2;

                request = genRequest(someSolidarityLeaderElectionApplicationItem(), SolidarityLeaderElectionApplyStatus.COMPLETE);
            }

            @Test
            @DisplayName("지원을 취소했다가 동일한 선출에 재지원 할 수 있다.")
            void shouldReturnSuccess() throws Exception {
                MvcResult mvcResult = callApi(status().isOk());

                final SolidarityLeaderElection leaderElection = itUtil.findSolidarityLeaderElectionByStockCode(stock.getCode()).orElseThrow();

                itUtil.assertSimpleOkay(mvcResult);
                assertSolidarityLeaderElection(leaderElection, currentKoreanDateTime, expectedCandidateRegistrationStartDateTime);
            }
        }

        @Nested
        class WhenElectionCompletedInThePastAndCreateNewElection {

            @BeforeEach
            void setUp() {
                createFinishedElectionInThePast();
            }

            private void createFinishedElectionInThePast() {
                LocalDateTime somePastDateTime = KoreanDateTimeUtil.getTodayLocalDateTime().minusDays(someIntegerBetween(1, 10));
                SolidarityLeaderElection solidarityElection = itUtil.createSolidarityElection(
                    stock.getCode(),
                    SolidarityLeaderElectionStatusGroup.FINISHED_ON_TIME_STATUS_GROUP,
                    somePastDateTime
                );
                SolidarityLeaderApplicant solidarityLeaderApplicant = itUtil.createSolidarityLeaderApplicant(
                    solidarity.getId(),
                    user.getId(),
                    SolidarityLeaderElectionApplyStatus.COMPLETE,
                    solidarityElection.getId()
                );
                solidarityLeaderApplicant.setCreatedAt(somePastDateTime);
                itUtil.updateSolidarityLeaderApplicant(solidarityLeaderApplicant);
            }

            @Nested
            class WhenSave {

                @BeforeEach
                void setUp() {
                    request = genRequest(someSolidarityLeaderElectionApplicationItem(), SolidarityLeaderElectionApplyStatus.SAVE);
                    candidateCount = 0;
                }


                @Test
                @DisplayName("과거에 종료된 주주대표 선출이 있고, 주주대표가 해임 된 후 기존 지원자가 다시 주주대표 선출에 지원하여 시작 대기중인 선출 과정을 생성할 수 있다.")
                void shouldReturnSuccess() throws Exception {
                    MvcResult mvcResult = callApi(status().isOk());

                    final Optional<SolidarityLeaderElection> electionOptional = itUtil.findSolidarityLeaderElectionByStockCode(stock.getCode());
                    assertThat(electionOptional.isPresent(), is(true));

                    itUtil.assertSimpleOkay(mvcResult);
                    assertSolidarityLeaderElectionIsPending();
                    assertNotSendSlackMessage();
                }

            }

            @Nested
            class WhenComplete {

                @BeforeEach
                void setUp() {
                    request = genRequest(someSolidarityLeaderElectionApplicationItem(), SolidarityLeaderElectionApplyStatus.COMPLETE);
                    candidateCount = 1;
                }

                @Test
                @DisplayName("과거에 종료된 주주대표 선출이 있고, 주주대표가 해임 된 후 기존에 지원했던 낙선자가 다시 주주대표 선출에 지원하여 선출 과정을 생성할 수 있다.")
                void shouldReturnSuccess() throws Exception {
                    MvcResult mvcResult = callApi(status().isOk());

                    final Optional<SolidarityLeaderElection> electionOptional = itUtil.findSolidarityLeaderElectionByStockCode(stock.getCode());
                    assertThat(electionOptional.isPresent(), is(true));

                    itUtil.assertSimpleOkay(mvcResult);
                    assertSolidarityLeaderElection(electionOptional.get(), currentKoreanDateTime, expectedCandidateRegistrationStartDateTime);
                    assertSendSlackMessage();
                }

            }
        }

        @Nested
        class WhenOnlyStakeOverMinThreshold {

            @BeforeEach
            void setUp() {
                solidarityDailySummary.setMemberCount(
                    someIntegerBetween(0, minThresholdMemberCount.intValue() - 1)
                );
                final SolidarityDailySummary updatedSolidarityDailySummary = itUtil.updateSolidarityDailySummary(solidarityDailySummary);
                mappingSolidarityAndSolidarityDailySummary(solidarity, updatedSolidarityDailySummary);

                request = genRequest(someSolidarityLeaderElectionApplicationItem(), someSolidarityLeaderElectionApplyStatus());
            }

            @Test
            void shouldReturnSuccess() throws Exception {
                MvcResult mvcResult = callApi(status().isOk());

                itUtil.assertSimpleOkay(mvcResult);
            }
        }

        @Nested
        class WhenOnlyMemberCountOverMinThreshold {

            @BeforeEach
            void setUp() {
                solidarityDailySummary.setStake(
                    someDoubleBetween(0.0, (double) minThresholdStake - 0.1)
                );
                final SolidarityDailySummary updatedSolidarityDailySummary = itUtil.updateSolidarityDailySummary(solidarityDailySummary);
                mappingSolidarityAndSolidarityDailySummary(solidarity, updatedSolidarityDailySummary);

                request = genRequest(someSolidarityLeaderElectionApplicationItem(), someSolidarityLeaderElectionApplyStatus());
            }

            @Test
            void shouldReturnSuccess() throws Exception {
                MvcResult mvcResult = callApi(status().isOk());

                itUtil.assertSimpleOkay(mvcResult);
            }
        }
    }

    @Nested
    class WhenFailed {

        @Nested
        class WhenUserNotHoldingStock {

            @BeforeEach
            void setUp() {
                stock = itUtil.createStock();
                request = genRequest(someSolidarityLeaderElectionApplicationItem(), SolidarityLeaderElectionApplyStatus.COMPLETE);
            }

            @Test
            @DisplayName("주주가 아닐 경우 지원할 수 없다.")
            void shouldReturnForbidden() throws Exception {
                MvcResult mvcResult = callApi(status().isForbidden());

                itUtil.assertErrorResponse(mvcResult, 403, "보유하고 있지 않은 주식입니다.");
                assertNotSendSlackMessage();
            }

        }

        @Nested
        class WhenUserAlreadyApplyComplete {

            @BeforeEach
            void setUp() {
                SolidarityLeaderElection solidarityElection = itUtil.createSolidarityElection(
                    stock.getCode(),
                    new SolidarityLeaderElectionStatusGroup(
                        SolidarityLeaderElectionStatus.CANDIDATE_REGISTRATION_PERIOD,
                        SolidarityLeaderElectionStatusDetails.IN_PROGRESS
                    ),
                    LocalDateTime.now()
                );
                itUtil.createSolidarityLeaderApplicant(
                    solidarity.getId(),
                    user.getId(),
                    SolidarityLeaderElectionApplyStatus.COMPLETE,
                    solidarityElection.getId()
                );

                request = genRequest(someSolidarityLeaderElectionApplicationItem(), SolidarityLeaderElectionApplyStatus.COMPLETE);
            }

            @Test
            void shouldReturnBadRequest() throws Exception {
                MvcResult mvcResult = callApi(status().isBadRequest());

                itUtil.assertErrorResponse(mvcResult, 400, "주주대표 지원 이력이 존재합니다. 임시저장의 경우 지원서를 수정하여 제출해주세요.");
                assertNotSendSlackMessage();
            }
        }

        @Nested
        class WhenUserSaveApplication {

            @BeforeEach
            void setUp() {
                final SolidarityLeaderElection solidarityElection = itUtil.createSolidarityElection(
                    stock.getCode(),
                    SolidarityLeaderElectionStatusGroup.CANDIDATE_REGISTER_PENDING_STATUS_GROUP
                );

                itUtil.createSolidarityLeaderApplicant(
                    solidarity.getId(),
                    user.getId(),
                    SolidarityLeaderElectionApplyStatus.SAVE,
                    solidarityElection.getId()
                );

                request = genRequest(someSolidarityLeaderElectionApplicationItem(), SolidarityLeaderElectionApplyStatus.COMPLETE);
            }

            @Test
            @DisplayName("이미 지원서를 임시 저장한 경우 지원서 생성 요청이 불가능하다.")
            void shouldReturnBadRequest() throws Exception {
                MvcResult mvcResult = callApi(status().isBadRequest());

                itUtil.assertErrorResponse(mvcResult, 400, "주주대표 지원 이력이 존재합니다. 임시저장의 경우 지원서를 수정하여 제출해주세요.");
                assertNotSendSlackMessage();
            }

        }

        @Nested
        class WhenAlreadyLeaderExist {

            @BeforeEach
            void setUp() {
                User leader = itUtil.createUser();
                itUtil.createSolidarityLeader(solidarity, leader.getId());

                request = genRequest(someSolidarityLeaderElectionApplicationItem(), SolidarityLeaderElectionApplyStatus.COMPLETE);
            }

            @Test
            void shouldReturnBadRequest() throws Exception {
                MvcResult mvcResult = callApi(status().isBadRequest());

                itUtil.assertErrorResponse(mvcResult, 400, "해당 연대에 이미 주주대표가 존재합니다.");
            }
        }

        @Nested
        class WhenApplicationItemLengthInvalidAndStatusComplete {

            @BeforeEach
            void setUp() {
                request = genRequest(someString(0, 50), SolidarityLeaderElectionApplyStatus.COMPLETE);
            }

            @Test
            void shouldReturnBadRequest() throws Exception {
                MvcResult mvcResult = callApi(status().isBadRequest());

                itUtil.assertErrorResponse(mvcResult, 400, "주주대표 지원서 각 항목은 모두 80자 이상 500자 이하로 입력해주세요.");
                assertNotSendSlackMessage();
            }
        }

        @Nested
        class WhenApplicationItemLengthInvalidAndStatusSave {

            @BeforeEach
            void setUp() {
                request = genRequest(someString(501, 1000), SolidarityLeaderElectionApplyStatus.SAVE);
            }

            @Test
            void shouldReturnBadRequest() throws Exception {
                MvcResult mvcResult = callApi(status().isBadRequest());

                itUtil.assertErrorResponse(mvcResult, 400, "주주대표 지원서 각 항목은 모두 500자 이하로 입력해주세요.");
                assertNotSendSlackMessage();
            }
        }

        @Nested
        class WhenNotApplyPeriod {

            @BeforeEach
            void setUp() {
                final LocalDateTime today = KoreanDateTimeUtil.getStartDateTimeOfToday();
                itUtil.createSolidarityElection(
                    stock.getCode(),
                    SolidarityLeaderElectionStatusGroup.VOTE_STATUS_GROUP,
                    today.minusDays(someIntegerBetween(SolidarityLeaderElectionProcedure.RECRUIT_APPLICANTS.getDurationDays(), 10))
                );

                request = genRequest(someSolidarityLeaderElectionApplicationItem(), SolidarityLeaderElectionApplyStatus.COMPLETE);
            }

            @Test
            void shouldReturnBadRequest() throws Exception {
                MvcResult mvcResult = callApi(status().isBadRequest());

                itUtil.assertErrorResponse(mvcResult, 400, "주주대표 지원 기간이 아닙니다.");
                assertNotSendSlackMessage();
            }
        }

        @Nested
        class WhenSolidarityMostRecentDailySummaryNotExists {

            @BeforeEach
            void setUp() {
                request = genRequest(someSolidarityLeaderElectionApplicationItem(), SolidarityLeaderElectionApplyStatus.COMPLETE);

                solidarity.setMostRecentDailySummary(null);
                itUtil.updateSolidarity(solidarity);
            }

            @Test
            void shouldReturnBadRequest() throws Exception {
                MvcResult mvcResult = callApi(status().isBadRequest());

                itUtil.assertErrorResponse(mvcResult, 400, "주주대표 지원이 불가합니다. 고객센터에 문의해주세요.");
                assertNotSendSlackMessage();
            }
        }

        @Nested
        class WhenApplicantIsBlockedUser {

            @BeforeEach
            void setUp() {
                request = genRequest(someSolidarityLeaderElectionApplicationItem(), SolidarityLeaderElectionApplyStatus.COMPLETE);

                final Long userId = user.getId();
                final String stockCode = stock.getCode();
                itUtil.createBlockedSolidarityLeaderApplicant(stockCode, userId);
            }

            @Test
            void shouldReturnBadRequest() throws Exception {
                MvcResult mvcResult = callApi(status().isBadRequest());

                itUtil.assertErrorResponse(mvcResult, 400, "해당 종목에 주주대표 지원이 불가능합니다. 관리자에게 문의해 주세요.");
            }
        }
    }

    @Nested
    class WhenApplicationConditionInvalid {

        @BeforeEach
        void setUp() {
            solidarityDailySummary.setStake(
                someDoubleBetween(0.0, (double) minThresholdStake - 0.1)
            );
            solidarityDailySummary.setMemberCount(
                someIntegerBetween(0, minThresholdMemberCount.intValue() - 1)
            );

            final SolidarityDailySummary updatedSolidarityDailySummary = itUtil.updateSolidarityDailySummary(solidarityDailySummary);
            mappingSolidarityAndSolidarityDailySummary(solidarity, updatedSolidarityDailySummary);

            request = genRequest(someSolidarityLeaderElectionApplicationItem(), someSolidarityLeaderElectionApplyStatus());
        }

        @Nested
        class AndElectionInCandidateRegistrationPeriod {

            @BeforeEach
            void setUp() {
                itUtil.createSolidarityElection(
                    stock.getCode(),
                    SolidarityLeaderElectionStatusGroup.CANDIDATE_REGISTER_STATUS_GROUP,
                    LocalDateTime.now()
                );
            }

            @Test
            void shouldReturnSuccess() throws Exception {
                MvcResult mvcResult = callApi(status().isOk());

                itUtil.assertSimpleOkay(mvcResult);
            }
        }

        @Nested
        class AndElectionInCandidateRegistrationPendingPeriod {

            @Test
            void shouldReturnBadRequest() throws Exception {
                MvcResult mvcResult = callApi(status().isBadRequest());

                itUtil.assertErrorResponse(mvcResult, 400, "주주대표 충족요건이 달성되지 않아 지원이 불가능합니다.");
            }
        }
    }

    private MvcResult callApi(ResultMatcher matcher) throws Exception {
        return mockMvc.perform(
                post(TARGET_API, stock.getCode())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapperUtil.toJson(request))
                    .header(AUTHORIZATION, "Bearer " + jwt)
            ).andExpect(matcher)
            .andReturn();
    }
}
