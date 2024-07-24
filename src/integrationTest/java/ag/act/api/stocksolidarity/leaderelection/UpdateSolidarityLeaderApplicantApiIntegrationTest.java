package ag.act.api.stocksolidarity.leaderelection;

import ag.act.dto.solidarity.election.SolidarityLeaderElectionStatusGroup;
import ag.act.entity.SolidarityDailySummary;
import ag.act.entity.SolidarityLeaderApplicant;
import ag.act.entity.User;
import ag.act.entity.solidarity.election.LeaderElectionCurrentDateTimeProvider;
import ag.act.entity.solidarity.election.SolidarityLeaderElection;
import ag.act.enums.SolidarityLeaderElectionProcedure;
import ag.act.util.KoreanDateTimeUtil;
import ag.act.util.ZoneIdUtil;
import org.hamcrest.Matchers;
import org.jetbrains.annotations.NotNull;
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
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static ag.act.TestUtil.someSolidarityLeaderElectionApplicationItem;
import static ag.act.TestUtil.someSolidarityLeaderElectionApplyStatus;
import static ag.act.enums.solidarity.election.SolidarityLeaderElectionApplyStatus.COMPLETE;
import static ag.act.enums.solidarity.election.SolidarityLeaderElectionApplyStatus.SAVE;
import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomDoubles.someDoubleBetween;
import static shiver.me.timbers.data.random.RandomIntegers.someIntegerBetween;
import static shiver.me.timbers.data.random.RandomLongs.someLongBetween;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@SuppressWarnings("checkstyle:LineLength")
class UpdateSolidarityLeaderApplicantApiIntegrationTest extends AbstractSolidarityLeaderElectionApiIntegrationTest {

    private static final String TARGET_API =
        "/api/stocks/{stockCode}/solidarity-leader-elections/{solidarityLeaderElectionId}/solidarity-leader-applicants/{solidarityLeaderApplicantId}";

    private Long solidarityLeaderApplicantId;
    private Long solidarityElectionId;
    private SolidarityLeaderElection solidarityLeaderElection;
    private LocalDateTime currentKoreanDateTime;
    private LocalDateTime expectedCandidateRegistrationStartDateTime;

    @Nested
    class WhenSuccess {
        private List<MockedStatic<?>> statics;

        @AfterEach
        void tearDown() {
            statics.forEach(MockedStatic::close);
        }

        @BeforeEach
        void setUp() {
            statics = List.of(mockStatic(LeaderElectionCurrentDateTimeProvider.class));
            final ZonedDateTime currentZonedDateTime = getCurrentZonedDateTime();
            currentKoreanDateTime = currentZonedDateTime.toLocalDateTime();
            expectedCandidateRegistrationStartDateTime = currentZonedDateTime.toLocalDateTime().minusHours(KOREAN_TIME_OFFSET);

            given(LeaderElectionCurrentDateTimeProvider.get())
                .willReturn(expectedCandidateRegistrationStartDateTime);
            given(LeaderElectionCurrentDateTimeProvider.getKoreanDateTime())
                .willReturn(currentZonedDateTime);

            createElection();
        }

        @Nested
        class WhenStatusSave {

            @BeforeEach
            void setUp() {
                solidarityLeaderApplicantId = itUtil.createSolidarityLeaderApplicant(
                    solidarity.getId(),
                    user.getId(),
                    SAVE,
                    solidarityElectionId
                ).getId();
                request = genRequest(someString(0, 50), SAVE);
            }

            @Nested
            class WhenSolidarityLeaderElectionNotExist {

                @Test
                @DisplayName("선출 투표가 시작되지 않았을 때 지원서를 임시 저장하는 경우, 선출 투표 생성 및 맵핑하여 저장한다.")
                void shouldReturnSuccess() throws Exception {
                    MvcResult mvcResult = callApi(status().isOk());

                    itUtil.assertSimpleOkay(mvcResult);
                    assertSolidarityLeaderApplicantElectionFromDatabase();
                    assertNotSendSlackMessage();
                }

                private void assertSolidarityLeaderApplicantElectionFromDatabase() {
                    final SolidarityLeaderApplicant solidarityLeaderApplicant =
                        itUtil.findSolidarityLeaderApplicant(solidarity.getId(), user.getId()).orElseThrow();

                    assertThat(solidarityLeaderApplicant.getSolidarityId(), is(solidarity.getId()));
                    assertThat(solidarityLeaderApplicant.getUserId(), is(user.getId()));
                    assertThat(solidarityLeaderApplicant.getApplyStatus(), is(SAVE));
                    assertThat(solidarityLeaderApplicant.getReasonsForApplying(), is(request.getReasonsForApply()));
                    assertThat(solidarityLeaderApplicant.getKnowledgeOfCompanyManagement(), is(request.getKnowledgeOfCompanyManagement()));
                    assertThat(solidarityLeaderApplicant.getGoals(), is(request.getGoals()));
                    assertThat(solidarityLeaderApplicant.getCommentsForStockHolder(), is(request.getCommentsForStockHolder()));
                    assertThat(solidarityLeaderApplicant.getSolidarityLeaderElectionId(), is(solidarityElectionId));
                }
            }

            @Nested
            class WhenSolidarityLeaderElectionExist {

                @BeforeEach
                void setUp() {
                    itUtil.startSolidarityLeaderElection(solidarityLeaderElection);
                }

                @Test
                @DisplayName("선출 투표가 진행 중일 때 지원서를 임시 저장하는 경우, 기존 선출 투표 데이터를 맵핑하여 저장한다.")
                void shouldReturnSuccess() throws Exception {
                    MvcResult mvcResult = callApi(status().isOk());

                    itUtil.assertSimpleOkay(mvcResult);
                    assertSolidarityLeaderApplicant(solidarityLeaderElection);
                    assertNotSendSlackMessage();
                }
            }
        }

        @Nested
        class WhenStatusComplete {

            @BeforeEach
            void setUp() {
                request = genRequest(someSolidarityLeaderElectionApplicationItem(), COMPLETE);
            }

            @Nested
            class WhenSolidarityLeaderElectionNotExist {

                @BeforeEach
                void setUp() {
                    candidateCount = 1;

                    solidarityLeaderApplicantId =
                        itUtil.createSolidarityLeaderApplicant(
                            solidarity.getId(),
                            user.getId(),
                            SAVE,
                            solidarityElectionId
                        ).getId();
                }

                @Test
                @DisplayName("선출이 시작되지 않았을 때 지원서를 완료하는 경우, 선출 투표 생성 및 매핑하여 저장한다.")
                void shouldReturnSuccess() throws Exception {
                    MvcResult mvcResult = callApi(status().isOk());

                    itUtil.assertSimpleOkay(mvcResult);

                    final Optional<SolidarityLeaderElection> electionOptional = itUtil.findSolidarityLeaderElectionByStockCode(stock.getCode());
                    assertThat(electionOptional.isPresent(), is(true));

                    assertSolidarityLeaderElection(electionOptional.get(), currentKoreanDateTime, expectedCandidateRegistrationStartDateTime);
                    assertSolidarityLeaderApplicant(electionOptional.get());
                    assertSendSlackMessage();
                }
            }

            @Nested
            class WhenSolidarityLeaderElectionExist {

                @BeforeEach
                void setUp() {
                    candidateCount = 2;

                    solidarityLeaderApplicantId =
                        itUtil.createSolidarityLeaderApplicant(
                            solidarity.getId(),
                            user.getId(),
                            COMPLETE,
                            solidarityElectionId
                        ).getId();

                    final LocalDateTime today = KoreanDateTimeUtil.getTodayLocalDate().atStartOfDay();
                    startSolidarityLeaderElection(
                        today.minusDays(someIntegerBetween(1, SolidarityLeaderElectionProcedure.RECRUIT_APPLICANTS.getDurationDays()))
                    );

                    User anotherUser = itUtil.createUser();
                    itUtil.createSolidarityLeaderApplicant(solidarity.getId(), anotherUser.getId());
                }

                @Test
                @DisplayName("주주대표 선출 프로세스가 진행 중일 때 지원서를 완료하는 경우, 기존 선출 투표 데이터를 맵핑하여 저장한다.")
                void shouldReturnSuccess() throws Exception {
                    MvcResult mvcResult = callApi(status().isOk());

                    itUtil.assertSimpleOkay(mvcResult);
                    assertSolidarityLeaderApplicant(solidarityLeaderElection);
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

                solidarityLeaderApplicantId =
                    itUtil.createSolidarityLeaderApplicant(
                        solidarity.getId(),
                        user.getId(),
                        someSolidarityLeaderElectionApplyStatus(),
                        solidarityElectionId
                    ).getId();
                request = genRequest(someSolidarityLeaderElectionApplicationItem(), COMPLETE);
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

                solidarityLeaderApplicantId =
                    itUtil.createSolidarityLeaderApplicant(
                        solidarity.getId(),
                        user.getId(),
                        someSolidarityLeaderElectionApplyStatus(),
                        solidarityElectionId
                    ).getId();
                request = genRequest(someSolidarityLeaderElectionApplicationItem(), COMPLETE);
            }

            @Test
            void shouldReturnSuccess() throws Exception {
                MvcResult mvcResult = callApi(status().isOk());

                itUtil.assertSimpleOkay(mvcResult);
            }
        }
    }

    private void startSolidarityLeaderElection(LocalDateTime candidateRegistrationStartDateTime) {
        itUtil.startSolidarityLeaderElection(
            solidarityLeaderElection,
            candidateRegistrationStartDateTime,
            candidateRegistrationStartDateTime.plusDays(SolidarityLeaderElectionProcedure.RECRUIT_APPLICANTS.getDurationDays())
        );
    }

    @Nested
    class WhenFailed {

        @BeforeEach
        void setUp() {
            createElection();
        }

        @Nested
        class WhenSolidarityLeaderApplicantNotExist {

            @BeforeEach
            void setUp() {
                solidarityLeaderApplicantId = someLongBetween(1000L, 999999L);
                request = genRequest(someSolidarityLeaderElectionApplicationItem(), COMPLETE);
            }

            @Test
            void shouldReturnNotFound() throws Exception {
                MvcResult mvcResult = callApi(status().isNotFound());

                itUtil.assertErrorResponse(mvcResult, 404, "지원서를 임시 저장하거나 제출한 내역이 없습니다.");
            }

        }

        @Nested
        class WhenUserIsNotApplicationWriter {

            @BeforeEach
            void setUp() {
                final User anotherUser = itUtil.createUser();

                solidarityLeaderApplicantId =
                    itUtil.createSolidarityLeaderApplicant(
                        solidarity.getId(),
                        anotherUser.getId(),
                        SAVE,
                        solidarityElectionId
                    ).getId();
                request = genRequest(someSolidarityLeaderElectionApplicationItem(), someSolidarityLeaderElectionApplyStatus());
            }

            @Test
            void shouldReturnBadRequest() throws Exception {
                MvcResult mvcResult = callApi(status().isBadRequest());

                itUtil.assertErrorResponse(mvcResult, 400, "본인이 작성한 지원서만 수정할 수 있습니다.");
            }
        }

        @Nested
        class WhenUpdateApplyStatusCompleteToSave {

            @BeforeEach
            void setUp() {
                solidarityLeaderApplicantId =
                    itUtil.createSolidarityLeaderApplicant(
                        solidarity.getId(),
                        user.getId(),
                        COMPLETE,
                        solidarityElectionId
                    ).getId();
                request = genRequest(someSolidarityLeaderElectionApplicationItem(), SAVE);
            }

            @Test
            void shouldReturnBadRequest() throws Exception {
                MvcResult mvcResult = callApi(status().isBadRequest());

                itUtil.assertErrorResponse(mvcResult, 400, "이미 제출한 지원서는 임시 저장할 수 없습니다.");
            }
        }

        @Nested
        class WhenUserNotHoldingStock {

            @BeforeEach
            void setUp() {
                stock = itUtil.createStock(); // create another stock
                solidarityLeaderApplicantId =
                    itUtil.createSolidarityLeaderApplicant(
                        solidarity.getId(),
                        user.getId(),
                        someSolidarityLeaderElectionApplyStatus(),
                        solidarityElectionId
                    ).getId();
                request = genRequest(someSolidarityLeaderElectionApplicationItem(), COMPLETE);
            }

            @Test
            void shouldReturnForbidden() throws Exception {
                MvcResult mvcResult = callApi(status().isForbidden());

                itUtil.assertErrorResponse(mvcResult, 403, "보유하고 있지 않은 주식입니다.");
            }
        }

        @Nested
        class WhenAlreadyLeaderExist {

            @BeforeEach
            void setUp() {
                User leader = itUtil.createUser();
                itUtil.createSolidarityLeader(solidarity, leader.getId());

                solidarityLeaderApplicantId =
                    itUtil.createSolidarityLeaderApplicant(
                        solidarity.getId(),
                        user.getId(),
                        someSolidarityLeaderElectionApplyStatus(),
                        solidarityElectionId
                    ).getId();
                request = genRequest(someSolidarityLeaderElectionApplicationItem(), COMPLETE);
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
                solidarityLeaderApplicantId =
                    itUtil.createSolidarityLeaderApplicant(
                        solidarity.getId(),
                        user.getId(),
                        someSolidarityLeaderElectionApplyStatus(),
                        solidarityElectionId
                    ).getId();
                request = genRequest(someString(0, 50), COMPLETE);
            }

            @Test
            void shouldReturnBadRequest() throws Exception {
                MvcResult mvcResult = callApi(status().isBadRequest());

                itUtil.assertErrorResponse(mvcResult, 400, "주주대표 지원서 각 항목은 모두 80자 이상 500자 이하로 입력해주세요.");
            }
        }

        @Nested
        class WhenApplicationItemLengthInvalidAndStatusSave {

            @BeforeEach
            void setUp() {
                solidarityLeaderApplicantId =
                    itUtil.createSolidarityLeaderApplicant(
                        solidarity.getId(),
                        user.getId(),
                        someSolidarityLeaderElectionApplyStatus(),
                        solidarityElectionId
                    ).getId();
                request = genRequest(someString(501, 1000), SAVE);
            }

            @Test
            void shouldReturnBadRequest() throws Exception {
                MvcResult mvcResult = callApi(status().isBadRequest());

                itUtil.assertErrorResponse(mvcResult, 400, "주주대표 지원서 각 항목은 모두 500자 이하로 입력해주세요.");
            }
        }

        @Nested
        class WhenNotApplyPeriod {

            @BeforeEach
            void setUp() {
                final ZonedDateTime currentZonedDateTime = getCurrentZonedDateTime();
                final LocalDateTime today = currentZonedDateTime.toLocalDateTime().truncatedTo(ChronoUnit.DAYS);
                itUtil.startSolidarityLeaderElection(
                    solidarityLeaderElection,
                    today.minusDays(someIntegerBetween(SolidarityLeaderElectionProcedure.RECRUIT_APPLICANTS.getDurationDays() + 10, 20)),
                    today.minusDays(someIntegerBetween(SolidarityLeaderElectionProcedure.RECRUIT_APPLICANTS.getDurationDays() + 1, 10))
                );

                solidarityLeaderApplicantId =
                    itUtil.createSolidarityLeaderApplicant(
                        solidarity.getId(),
                        user.getId(),
                        someSolidarityLeaderElectionApplyStatus(),
                        solidarityElectionId
                    ).getId();
                request = genRequest(someSolidarityLeaderElectionApplicationItem(), COMPLETE);
            }

            @Test
            void shouldReturnBadRequest() throws Exception {
                MvcResult mvcResult = callApi(status().isBadRequest());

                itUtil.assertErrorResponse(mvcResult, 400, "주주대표 지원 기간이 아닙니다.");
            }
        }

        @Nested
        class WhenSolidarityMostRecentDailySummaryNotExists {

            @BeforeEach
            void setUp() {
                solidarityLeaderApplicantId =
                    itUtil.createSolidarityLeaderApplicant(
                        solidarity.getId(),
                        user.getId(),
                        someSolidarityLeaderElectionApplyStatus(),
                        solidarityElectionId
                    ).getId();
                request = genRequest(someSolidarityLeaderElectionApplicationItem(), COMPLETE);

                solidarity.setMostRecentDailySummary(null);
                itUtil.updateSolidarity(solidarity);
            }

            @Test
            void shouldReturnBadRequest() throws Exception {
                MvcResult mvcResult = callApi(status().isBadRequest());

                itUtil.assertErrorResponse(mvcResult, 400, "주주대표 지원이 불가합니다. 고객센터에 문의해주세요.");
            }
        }
    }

    @Nested
    class WhenApplicationConditionInvalid {

        @BeforeEach
        void setUp() {
            createElection();

            solidarityLeaderApplicantId =
                itUtil.createSolidarityLeaderApplicant(
                    solidarity.getId(),
                    user.getId(),
                    SAVE,
                    solidarityElectionId
                ).getId();
            request = genRequest(someSolidarityLeaderElectionApplicationItem(), someSolidarityLeaderElectionApplyStatus());

            solidarityDailySummary.setStake(
                someDoubleBetween(0.0, (double) minThresholdStake - 0.1)
            );
            solidarityDailySummary.setMemberCount(
                someIntegerBetween(0, minThresholdMemberCount.intValue() - 1)
            );
            final SolidarityDailySummary updatedSolidarityDailySummary = itUtil.updateSolidarityDailySummary(solidarityDailySummary);
            mappingSolidarityAndSolidarityDailySummary(solidarity, updatedSolidarityDailySummary);
        }

        @Nested
        class AndElectionInCandidateRegistrationPeriod {

            @BeforeEach
            void setUp() {
                itUtil.startSolidarityLeaderElection(solidarityLeaderElection);
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

    private void assertSolidarityLeaderApplicant(SolidarityLeaderElection election) {
        Optional<SolidarityLeaderApplicant> solidarityLeaderApplicantOptional =
            itUtil.findSolidarityLeaderApplicant(solidarity.getId(), user.getId());

        assertThat(solidarityLeaderApplicantOptional.isPresent(), Matchers.is(true));

        final SolidarityLeaderApplicant solidarityLeaderApplicant = solidarityLeaderApplicantOptional.get();
        assertThat(solidarityLeaderApplicant.getSolidarityId(), is(solidarity.getId()));
        assertThat(solidarityLeaderApplicant.getUserId(), is(user.getId()));
        assertThat(solidarityLeaderApplicant.getApplyStatus().name(), is(request.getApplyStatus()));
        assertThat(solidarityLeaderApplicant.getReasonsForApplying(), is(request.getReasonsForApply()));
        assertThat(solidarityLeaderApplicant.getKnowledgeOfCompanyManagement(), is(request.getKnowledgeOfCompanyManagement()));
        assertThat(solidarityLeaderApplicant.getGoals(), is(request.getGoals()));
        assertThat(solidarityLeaderApplicant.getCommentsForStockHolder(), is(request.getCommentsForStockHolder()));
        assertThat(solidarityLeaderApplicant.getSolidarityLeaderElectionId(), is(election.getId()));
    }

    private MvcResult callApi(ResultMatcher matcher) throws Exception {
        return mockMvc.perform(
                patch(TARGET_API, stock.getCode(), solidarityElectionId, solidarityLeaderApplicantId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapperUtil.toJson(request))
                    .headers(headers(jwt(jwt)))
            ).andExpect(matcher)
            .andReturn();
    }

    private void createElection() {
        solidarityLeaderElection = itUtil.createSolidarityElection(
            stock.getCode(),
            SolidarityLeaderElectionStatusGroup.CANDIDATE_REGISTER_PENDING_STATUS_GROUP
        );
        solidarityElectionId = solidarityLeaderElection.getId();
    }

    @NotNull
    private ZonedDateTime getCurrentZonedDateTime() {
        return ZonedDateTime.now(ZoneIdUtil.getSeoulZoneId());
    }
}
