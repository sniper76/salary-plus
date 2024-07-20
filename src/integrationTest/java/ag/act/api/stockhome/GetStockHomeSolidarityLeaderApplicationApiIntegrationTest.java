package ag.act.api.stockhome;

import ag.act.dto.solidarity.election.SolidarityLeaderElectionStatusGroup;
import ag.act.entity.SolidarityLeaderApplicant;
import ag.act.entity.User;
import ag.act.entity.solidarity.election.LeaderElectionCurrentDateTimeProvider;
import ag.act.entity.solidarity.election.SolidarityLeaderElection;
import ag.act.enums.SolidarityLeaderElectionProcedure;
import ag.act.enums.solidarity.election.SolidarityLeaderElectionApplyStatus;
import ag.act.model.SimpleStockResponse;
import ag.act.model.SolidarityLeaderApplicationResponse;
import ag.act.model.StockHomeResponse;
import ag.act.util.DateTimeUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;

import static ag.act.TestUtil.someLocalDateTimeInThePast;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomIntegers.someIntegerBetween;

class GetStockHomeSolidarityLeaderApplicationApiIntegrationTest extends GetStockHomeApiIntegrationTest {

    @BeforeEach
    void setUp() {
        given(LeaderElectionCurrentDateTimeProvider.get())
            .willReturn(DateTimeUtil.getTodayLocalDateTime());
    }

    @Nested
    class WhenElectionNotExist {

        @Nested
        class WhenUserSaveApplication {

            private SolidarityLeaderApplicant solidarityLeaderApplicant;

            @BeforeEach
            void setUp() {

                SolidarityLeaderElection solidarityElection = itUtil.createSolidarityElection(
                    stock.getCode(),
                    SolidarityLeaderElectionStatusGroup.CANDIDATE_REGISTER_PENDING_STATUS_GROUP
                );

                solidarityLeaderApplicant = itUtil.createSolidarityLeaderApplicant(
                    solidarity.getId(),
                    currentUser.getId(),
                    SolidarityLeaderElectionApplyStatus.SAVE,
                    solidarityElection.getId()
                );
            }

            @Test
            void shouldReturnApplyHistory() throws Exception {
                MvcResult mvcResult = callApi(status().isOk());
                StockHomeResponse response = getResponse(mvcResult);

                assertSolidarityLeaderApplicant(response.getLeaderApplication(), solidarityLeaderApplicant);
                assertStockResponse(response.getStock());
            }

        }

        @Nested
        class WhenUserNotCreatedApplication {

            @Test
            void shouldNotReturnApplyHistory() throws Exception {
                MvcResult mvcResult = callApi(status().isOk());
                StockHomeResponse response = getResponse(mvcResult);

                assertSolidarityLeaderApplyHistoryNotExist(response);
                assertStockResponse(response.getStock());
            }

        }
    }

    @Nested
    class WhenElectionIsFinished {

        @Nested
        class WhenUserNotApplied {

            @BeforeEach
            void setUp() {
                itUtil.createSolidarityElection(
                    stock.getCode(),
                    SolidarityLeaderElectionStatusGroup.FINISHED_ON_TIME_STATUS_GROUP,
                    someLocalDateTimeInThePast()
                );
            }

            @Test
            void shouldNotReturnApplyHistory() throws Exception {
                MvcResult mvcResult = callApi(status().isOk());
                StockHomeResponse response = getResponse(mvcResult);

                assertSolidarityLeaderApplyHistoryNotExist(response);
                assertStockResponse(response.getStock());
            }
        }

        @Nested
        class WhenUserApplied {

            @BeforeEach
            void setUp() {
                SolidarityLeaderElection solidarityElection = itUtil.createSolidarityElection(
                    stock.getCode(),
                    SolidarityLeaderElectionStatusGroup.FINISHED_ON_TIME_STATUS_GROUP,
                    someLocalDateTimeInThePast()
                );
                itUtil.createSolidarityLeaderApplicant(
                    solidarity.getId(),
                    currentUser.getId(),
                    SolidarityLeaderElectionApplyStatus.COMPLETE,
                    solidarityElection.getId()
                );
            }

            @Test
            void shouldNotReturnApplyHistory() throws Exception {
                MvcResult mvcResult = callApi(status().isOk());
                StockHomeResponse response = getResponse(mvcResult);

                assertSolidarityLeaderApplyHistoryNotExist(response);
                assertStockResponse(response.getStock());
            }

        }
    }

    @Nested
    class WhenElectionIsInCandidateRegistrationPeriod {

        private Long solidarityLeaderElectionId;

        @BeforeEach
        void setUp() {
            final LocalDateTime today = DateTimeUtil.getTodayLocalDateTime();
            SolidarityLeaderElection solidarityLeaderElection = createElectionAndGet(
                today,
                SolidarityLeaderElectionStatusGroup.CANDIDATE_REGISTER_STATUS_GROUP
            );
            solidarityLeaderElectionId = solidarityLeaderElection.getId();
        }

        @Nested
        class WhenUserSaveApplication {

            @BeforeEach
            void setUp() {
                SolidarityLeaderElection solidarityLeaderElection = createElectionAndGet(
                    DateTimeUtil.getTodayLocalDateTime(),
                    SolidarityLeaderElectionStatusGroup.CANDIDATE_REGISTER_STATUS_GROUP
                );

                itUtil.createSolidarityLeaderApplicant(
                    solidarity.getId(),
                    currentUser.getId(),
                    SolidarityLeaderElectionApplyStatus.SAVE,
                    solidarityLeaderElection.getId()
                );
            }

            @Test
            void shouldReturnApplyHistory() throws Exception {
                MvcResult mvcResult = callApi(status().isOk());
                StockHomeResponse response = getResponse(mvcResult);

                assertSolidarityLeaderApplyHistoryFromDatabase(response);
                assertStockResponse(response.getStock());
            }
        }

        @Nested
        class WhenUserCompleteApplication {

            @BeforeEach
            void setUp() {
                itUtil.createSolidarityLeaderApplicant(
                    solidarity.getId(),
                    currentUser.getId(),
                    SolidarityLeaderElectionApplyStatus.COMPLETE,
                    solidarityLeaderElectionId
                );
            }

            @Test
            void shouldReturnApplyHistory() throws Exception {
                MvcResult mvcResult = callApi(status().isOk());
                StockHomeResponse response = getResponse(mvcResult);

                assertSolidarityLeaderApplyHistoryFromDatabase(response);
                assertStockResponse(response.getStock());
            }
        }

        @Nested
        class WhenDeletedByUserApplication {

            @BeforeEach
            void setUp() {
                SolidarityLeaderElection solidarityLeaderElection = createElectionAndGet(
                    DateTimeUtil.getTodayLocalDateTime(),
                    SolidarityLeaderElectionStatusGroup.CANDIDATE_REGISTER_STATUS_GROUP
                );

                itUtil.createSolidarityLeaderApplicant(
                    solidarity.getId(),
                    currentUser.getId(),
                    SolidarityLeaderElectionApplyStatus.DELETED_BY_USER,
                    solidarityLeaderElection.getId()
                );
            }

            @Test
            void shouldReturnEmptyApplyHistory() throws Exception {
                MvcResult mvcResult = callApi(status().isOk());
                StockHomeResponse response = getResponse(mvcResult);

                assertSolidarityLeaderApplyHistoryNotExist(response);
                assertStockResponse(response.getStock());
            }
        }

        private void assertSolidarityLeaderApplyHistoryFromDatabase(StockHomeResponse response) {
            SolidarityLeaderApplicationResponse leaderApplyHistory = response.getLeaderApplication();

            SolidarityLeaderApplicant solidarityLeaderApplicant = itUtil.findSolidarityLeaderApplicant(solidarity.getId(), currentUser.getId())
                .orElseThrow();

            assertSolidarityLeaderApplicant(leaderApplyHistory, solidarityLeaderApplicant);
            assertStockResponse(response.getStock());
        }
    }

    @Nested
    class WhenElectionIsNotInCandidateRegisterPeriod {

        @Nested
        class WhenUserSaveApplication {

            @BeforeEach
            void setUp() {
                final LocalDateTime somePastDateTime = DateTimeUtil.getTodayLocalDateTime()
                    .minusDays(SolidarityLeaderElectionProcedure.RECRUIT_APPLICANTS.getDurationDays());
                SolidarityLeaderElection solidarityLeaderElection = createElectionAndGet(
                    somePastDateTime,
                    SolidarityLeaderElectionStatusGroup.VOTE_STATUS_GROUP
                );

                itUtil.createSolidarityLeaderApplicant(
                    solidarity.getId(),
                    currentUser.getId(),
                    SolidarityLeaderElectionApplyStatus.SAVE,
                    solidarityLeaderElection.getId()
                );
            }

            @Test
            void shouldNotReturnApplyHistory() throws Exception {
                MvcResult mvcResult = callApi(status().isOk());
                StockHomeResponse response = getResponse(mvcResult);

                assertSolidarityLeaderApplyHistoryNotExist(response);
                assertStockResponse(response.getStock());
            }
        }

        @Nested
        class WhenUserCompleteApplication {

            @BeforeEach
            void setUp() {
                LocalDateTime somePastDateTime = DateTimeUtil.getTodayLocalDateTime()
                    .minusDays(SolidarityLeaderElectionProcedure.RECRUIT_APPLICANTS.getDurationDays());
                SolidarityLeaderElection solidarityLeaderElection = createElectionAndGet(
                    somePastDateTime,
                    SolidarityLeaderElectionStatusGroup.VOTE_STATUS_GROUP
                );
                itUtil.createSolidarityLeaderApplicant(
                    solidarity.getId(),
                    currentUser.getId(),
                    SolidarityLeaderElectionApplyStatus.COMPLETE,
                    solidarityLeaderElection.getId()
                );
            }

            @Test
            void shouldNotReturnApplyHistory() throws Exception {
                MvcResult mvcResult = callApi(status().isOk());
                StockHomeResponse response = getResponse(mvcResult);

                assertSolidarityLeaderApplyHistoryNotExist(response);
                assertStockResponse(response.getStock());
            }
        }
    }

    @Nested
    class WhenSolidarityLeaderExist {

        @BeforeEach
        void setUp() {
            User someUser = itUtil.createUser();
            itUtil.createSolidarityLeader(solidarity, someUser.getId());
        }

        @Test
        void shouldNotReturnApplyHistory() throws Exception {
            MvcResult mvcResult = callApi(status().isOk());
            StockHomeResponse response = getResponse(mvcResult);

            assertSolidarityLeaderApplyHistoryNotExist(response);
            assertStockResponse(response.getStock());
        }

    }

    @Nested
    class WhenApplicationExistInThePastAndUserHasNewApplication {

        private SolidarityLeaderApplicant expectedSolidarityLeaderApplicant;

        @BeforeEach
        void setUp() {
            final LocalDateTime somePastDateTime = DateTimeUtil.getTodayLocalDateTime().minusYears(someIntegerBetween(1, 5));
            SolidarityLeaderElection pastSolidarityLeaderElection = createElectionAndGet(
                somePastDateTime, SolidarityLeaderElectionStatusGroup.FINISHED_ON_TIME_STATUS_GROUP);

            itUtil.createSolidarityLeaderApplicant(
                solidarity.getId(),
                currentUser.getId(),
                SolidarityLeaderElectionApplyStatus.COMPLETE,
                pastSolidarityLeaderElection.getId()
            );

            final LocalDateTime today = DateTimeUtil.getTodayLocalDateTime();
            final SolidarityLeaderElection newSolidarityLeaderElection = createElectionAndGet(
                today,
                SolidarityLeaderElectionStatusGroup.CANDIDATE_REGISTER_STATUS_GROUP
            );

            expectedSolidarityLeaderApplicant = itUtil.createSolidarityLeaderApplicant(
                solidarity.getId(),
                currentUser.getId(),
                SolidarityLeaderElectionApplyStatus.COMPLETE,
                newSolidarityLeaderElection.getId()
            );
        }

        @Test
        void shouldReturnPreviousSolidarityLeaderApplication() throws Exception {
            MvcResult mvcResult = callApi(status().isOk());
            StockHomeResponse response = getResponse(mvcResult);

            SolidarityLeaderApplicationResponse leaderApplyHistory = response.getLeaderApplication();
            assertSolidarityLeaderApplicant(leaderApplyHistory, expectedSolidarityLeaderApplicant);
            assertStockResponse(response.getStock());
        }

    }

    private SolidarityLeaderElection createElectionAndGet(LocalDateTime dateTime, SolidarityLeaderElectionStatusGroup statusGroup) {
        final LocalDateTime candidateStartDateTime = dateTime.minusDays(
            someIntegerBetween(1, SolidarityLeaderElectionProcedure.RECRUIT_APPLICANTS.getDurationDays())
        );
        return itUtil.createSolidarityElection(stock.getCode(), statusGroup, candidateStartDateTime);
    }

    private void assertSolidarityLeaderApplicant(
        SolidarityLeaderApplicationResponse response,
        SolidarityLeaderApplicant expectedSolidarityLeaderApplicant
    ) {
        assertThat(response.getSolidarityLeaderElectionId(), is(expectedSolidarityLeaderApplicant.getSolidarityLeaderElectionId()));
        assertThat(response.getSolidarityLeaderApplicantId(), is(expectedSolidarityLeaderApplicant.getId()));
        assertThat(response.getApplyStatus(), is(expectedSolidarityLeaderApplicant.getApplyStatus().name()));
    }

    private void assertSolidarityLeaderApplyHistoryNotExist(StockHomeResponse response) {
        assertThat(response.getLeaderApplication(), is(nullValue()));
    }

    private void assertStockResponse(SimpleStockResponse stockResponse) {
        assertThat(stockResponse.getCode(), is(stock.getCode()));
        assertThat(stockResponse.getName(), is(stock.getName()));
    }
}
