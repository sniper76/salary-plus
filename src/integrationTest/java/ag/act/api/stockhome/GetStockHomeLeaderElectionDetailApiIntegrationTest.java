package ag.act.api.stockhome;

import ag.act.dto.solidarity.SolidarityLeaderElectionPeriod;
import ag.act.dto.solidarity.election.SolidarityLeaderElectionStatusGroup;
import ag.act.entity.solidarity.election.LeaderElectionCurrentDateTimeProvider;
import ag.act.entity.solidarity.election.SolidarityLeaderElection;
import ag.act.enums.SolidarityLeaderElectionProcedure;
import ag.act.enums.solidarity.election.SolidarityLeaderElectionApplyStatus;
import ag.act.enums.solidarity.election.SolidarityLeaderElectionStatus;
import ag.act.model.LeaderElectionDetailResponse;
import ag.act.model.LeaderElectionProcessDetailResponse;
import ag.act.model.LeaderElectionProcessLabelResponse;
import ag.act.model.LeaderElectionProcessResponse;
import ag.act.model.StockHomeResponse;
import ag.act.util.DateTimeUtil;
import ag.act.util.KoreanDateTimeUtil;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static ag.act.TestUtil.assertTime;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomIntegers.someIntegerBetween;

class GetStockHomeLeaderElectionDetailApiIntegrationTest extends GetStockHomeApiIntegrationTest {

    @Nested
    class WhenHasNotLeaderElectionDetail {

        @Test
        void shouldReturnSuccess() throws Exception {
            final StockHomeResponse result = getResponse(callApi(status().isOk()));

            final LeaderElectionDetailResponse leaderElectionDetail = result.getLeaderElectionDetail();

            assertThat(leaderElectionDetail, nullValue());
        }
    }

    @Nested
    class WhenHaveLeaderElectionDetail {
        private final int candidateCount = 1;
        private LocalDateTime todayLocalDateTime;
        private long durationDays = 0L;

        @Nested
        class WhenProcessingCandidateRegistration {
            private SolidarityLeaderElection solidarityLeaderElection;

            @Nested
            class WhenNormal {

                @BeforeEach
                void setUp() {
                    todayLocalDateTime = getTodayLocalDateTimeWithMocking();
                    solidarityLeaderElection = createElectionAndGet(
                        todayLocalDateTime,
                        SolidarityLeaderElectionStatusGroup.CANDIDATE_REGISTER_STATUS_GROUP,
                        candidateCount,
                        new SolidarityLeaderElectionPeriod(
                            todayLocalDateTime.minusDays(1),
                            todayLocalDateTime.plusDays(4),
                            todayLocalDateTime.plusDays(4),
                            todayLocalDateTime.plusDays(6)
                        )
                    );

                    itUtil.createSolidarityLeaderApplicant(
                        solidarity.getId(),
                        currentUser.getId(),
                        SolidarityLeaderElectionApplyStatus.COMPLETE,
                        solidarityLeaderElection.getId()
                    );

                    durationDays = 5;
                }

                @Test
                void shouldReturnSuccess() throws Exception {
                    final StockHomeResponse result = getResponse(callApi(status().isOk()));

                    final LeaderElectionDetailResponse leaderElectionDetail = result.getLeaderElectionDetail();

                    assertResponse(
                        solidarityLeaderElection,
                        leaderElectionDetail,
                        CandidateRegistrationTextAndColor.of("%s일 남음".formatted(durationDays), "#FF0000"),
                        VoteTextAndColor.nullValue(),
                        FinishTextAndColor.nullValue(),
                        false
                    );
                }
            }

            @Nested
            class WhenCandidateRegisterEndOfSameDay {

                @BeforeEach
                void setUp() {
                    todayLocalDateTime = getTodayLocalDateTimeWithMocking();
                    final LocalDateTime candidateRegisterEndDateTime = todayLocalDateTime.withHour(15)
                        .withMinute(0)
                        .withSecond(0)
                        .withNano(0);

                    solidarityLeaderElection = createElectionAndGet(
                        todayLocalDateTime,
                        SolidarityLeaderElectionStatusGroup.CANDIDATE_REGISTER_STATUS_GROUP,
                        candidateCount,
                        new SolidarityLeaderElectionPeriod(
                            todayLocalDateTime.minusDays(4),
                            candidateRegisterEndDateTime,
                            candidateRegisterEndDateTime,
                            candidateRegisterEndDateTime.plusDays(3)
                        )
                    );

                    itUtil.createSolidarityLeaderApplicant(
                        solidarity.getId(),
                        currentUser.getId(),
                        SolidarityLeaderElectionApplyStatus.COMPLETE,
                        solidarityLeaderElection.getId()
                    );

                    durationDays = 1;
                }

                @Test
                void shouldReturnSuccess() throws Exception {
                    final StockHomeResponse result = getResponse(callApi(status().isOk()));

                    final LeaderElectionDetailResponse leaderElectionDetail = result.getLeaderElectionDetail();

                    assertResponse(
                        solidarityLeaderElection,
                        leaderElectionDetail,
                        CandidateRegistrationTextAndColor.of("오늘 마감", "#FF0000"),
                        VoteTextAndColor.nullValue(),
                        FinishTextAndColor.nullValue(),
                        false
                    );
                }
            }
        }

        @Nested
        class WhenProcessingVoting {
            private SolidarityLeaderElection solidarityLeaderElection;

            @Nested
            class WhenNormal {

                @BeforeEach
                void setUp() {
                    todayLocalDateTime = getTodayLocalDateTimeWithMocking();
                    solidarityLeaderElection = createElectionAndGet(
                        todayLocalDateTime, SolidarityLeaderElectionStatusGroup.VOTE_STATUS_GROUP);

                    solidarityLeaderElection = createElectionAndGet(
                        todayLocalDateTime,
                        SolidarityLeaderElectionStatusGroup.VOTE_STATUS_GROUP,
                        candidateCount,
                        new SolidarityLeaderElectionPeriod(
                            todayLocalDateTime.minusDays(3),
                            todayLocalDateTime.minusMinutes(1),
                            todayLocalDateTime.minusMinutes(1),
                            todayLocalDateTime.plusDays(4)
                        )
                    );

                    itUtil.createSolidarityLeaderApplicant(
                        solidarity.getId(),
                        currentUser.getId(),
                        SolidarityLeaderElectionApplyStatus.COMPLETE,
                        solidarityLeaderElection.getId()
                    );

                    durationDays = 5;
                }

                @Test
                void shouldReturnSuccess() throws Exception {
                    final StockHomeResponse result = getResponse(callApi(status().isOk()));

                    final LeaderElectionDetailResponse leaderElectionDetail = result.getLeaderElectionDetail();

                    assertResponse(
                        solidarityLeaderElection,
                        leaderElectionDetail,
                        CandidateRegistrationTextAndColor.of("마감", "#0E9F33"),
                        VoteTextAndColor.of("%s일 남음".formatted(durationDays), "#FF0000"),
                        FinishTextAndColor.nullValue(),
                        false
                    );
                }
            }

            @Nested
            class WhenVoteEndOnTheSameDay {

                @BeforeEach
                void setUp() {
                    todayLocalDateTime = getTodayLocalDateTimeWithMocking();

                    solidarityLeaderElection = createElectionAndGet(
                        todayLocalDateTime,
                        SolidarityLeaderElectionStatusGroup.VOTE_STATUS_GROUP,
                        candidateCount,
                        new SolidarityLeaderElectionPeriod(
                            todayLocalDateTime.minusDays(6),
                            todayLocalDateTime.minusDays(2),
                            todayLocalDateTime.minusDays(2),
                            todayLocalDateTime.withHour(15).withMinute(0).withSecond(0).withNano(0)
                        )
                    );

                    itUtil.createSolidarityLeaderApplicant(
                        solidarity.getId(),
                        currentUser.getId(),
                        SolidarityLeaderElectionApplyStatus.COMPLETE,
                        solidarityLeaderElection.getId()
                    );

                    durationDays = 1;
                }

                @Test
                void shouldReturnSuccess() throws Exception {
                    final StockHomeResponse result = getResponse(callApi(status().isOk()));

                    final LeaderElectionDetailResponse leaderElectionDetail = result.getLeaderElectionDetail();

                    assertResponse(
                        solidarityLeaderElection,
                        leaderElectionDetail,
                        CandidateRegistrationTextAndColor.of("마감", "#0E9F33"),
                        VoteTextAndColor.of("오늘 마감", "#FF0000"),
                        FinishTextAndColor.nullValue(),
                        false
                    );
                }
            }
        }

        @Nested
        class WhenElectionCompletedAndClosing {
            @Nested
            class FinishedOnTime {

                private SolidarityLeaderElection solidarityLeaderElection;

                @BeforeEach
                void setUp() {
                    todayLocalDateTime = getTodayLocalDateTimeWithMocking();

                    solidarityLeaderElection = createElectionAndGet(
                        todayLocalDateTime,
                        SolidarityLeaderElectionStatusGroup.FINISHED_ON_TIME_STATUS_GROUP,
                        candidateCount,
                        new SolidarityLeaderElectionPeriod(
                            todayLocalDateTime.minusDays(6),
                            todayLocalDateTime.minusDays(4),
                            todayLocalDateTime.minusDays(3),
                            todayLocalDateTime
                        ),
                        todayLocalDateTime
                    );

                    itUtil.createSolidarityLeaderApplicant(
                        solidarity.getId(),
                        currentUser.getId(),
                        SolidarityLeaderElectionApplyStatus.COMPLETE,
                        solidarityLeaderElection.getId()
                    );
                }

                @Test
                void shouldReturnSuccess() throws Exception {
                    final StockHomeResponse result = getResponse(callApi(status().isOk()));

                    final LeaderElectionDetailResponse leaderElectionDetail = result.getLeaderElectionDetail();

                    assertResponse(
                        solidarityLeaderElection,
                        leaderElectionDetail,
                        CandidateRegistrationTextAndColor.of("마감", "#0E9F33"),
                        VoteTextAndColor.of("마감", "#0E9F33"),
                        FinishTextAndColor.of("기간마감", "#0E9F33"),
                        true
                    );
                }
            }

            @Nested
            class FinishedEarly {

                private SolidarityLeaderElection solidarityLeaderElection;

                @BeforeEach
                void setUp() {
                    todayLocalDateTime = getTodayLocalDateTimeWithMocking();

                    solidarityLeaderElection = createElectionAndGet(
                        todayLocalDateTime,
                        SolidarityLeaderElectionStatusGroup.FINISHED_EARLY_STATUS_GROUP,
                        candidateCount,
                        new SolidarityLeaderElectionPeriod(
                            todayLocalDateTime.minusDays(6),
                            todayLocalDateTime.minusDays(4),
                            todayLocalDateTime.minusDays(3),
                            todayLocalDateTime.plusMinutes(someIntegerBetween(1, 59))
                        ),
                        todayLocalDateTime // 조기마감이므로 현재보다 이후 시간이 투표 종료 시간이어야 한다.
                    );

                    itUtil.createSolidarityLeaderApplicant(
                        solidarity.getId(),
                        currentUser.getId(),
                        SolidarityLeaderElectionApplyStatus.COMPLETE,
                        solidarityLeaderElection.getId()
                    );
                }

                @Test
                void shouldReturnSuccess() throws Exception {
                    final StockHomeResponse result = getResponse(callApi(status().isOk()));

                    final LeaderElectionDetailResponse leaderElectionDetail = result.getLeaderElectionDetail();

                    assertResponse(
                        solidarityLeaderElection,
                        leaderElectionDetail,
                        CandidateRegistrationTextAndColor.of("마감", "#0E9F33"),
                        VoteTextAndColor.of("마감", "#0E9F33"),
                        FinishTextAndColor.of("조기마감", "#0E9F33"),
                        true
                    );
                }
            }
        }

        private void assertResponse(
            SolidarityLeaderElection solidarityLeaderElection,
            LeaderElectionDetailResponse leaderElectionDetail,
            TextAndColor candidatePeriodTextAndColor,
            TextAndColor votePeriodTextAndColor,
            TextAndColor finishTextAndColor,
            boolean isFinished
        ) {
            assertThat(leaderElectionDetail, notNullValue());
            assertThat(leaderElectionDetail.getSolidarityLeaderElectionId(), is(solidarityLeaderElection.getId()));
            assertThat(leaderElectionDetail.getElectionStatus(), is(solidarityLeaderElection.getElectionStatus().name()));
            assertTime(leaderElectionDetail.getStartDate(), solidarityLeaderElection.getCandidateRegistrationStartDateTime());

            if (solidarityLeaderElection.getVoteClosingDateTime() != null) {
                assertTime(leaderElectionDetail.getEndDate(), solidarityLeaderElection.getVoteClosingDateTime());
            } else {
                assertTime(leaderElectionDetail.getEndDate(), solidarityLeaderElection.getVoteEndDateTime().minusSeconds(1));
            }

            final List<LeaderElectionProcessResponse> electionProcesses = leaderElectionDetail.getElectionProcesses();
            final List<SolidarityLeaderElectionStatus> leaderElectionStatuses = Arrays
                .stream(SolidarityLeaderElectionStatus.values())
                .filter(status -> !status.isPendingStatus())
                .toList();

            assertCandidateResponse(
                solidarityLeaderElection,
                electionProcesses.get(0),
                leaderElectionStatuses.get(0),
                candidatePeriodTextAndColor
            );

            assertVoteResponse(
                solidarityLeaderElection,
                electionProcesses.get(1),
                votePeriodTextAndColor
            );

            if (isFinished) {
                assertFinishedResponse(electionProcesses.get(2), finishTextAndColor);
                assertTime(leaderElectionDetail.getEndDate(), solidarityLeaderElection.getVoteClosingDateTime());
            } else {
                assertIsNotFinishedResponse(electionProcesses.get(2));
            }
        }

        private void assertFinishedResponse(
            LeaderElectionProcessResponse electionProcess,
            TextAndColor finishTextAndColor
        ) {
            final LeaderElectionProcessDetailResponse electionProcessDetail = electionProcess.getDetail();
            final LeaderElectionProcessLabelResponse electionProcessLabel = electionProcess.getLabel();

            assertThat(electionProcessDetail, nullValue());

            if (finishTextAndColor.isNull()) {
                assertThat(electionProcessLabel, nullValue());
            } else {
                assertThat(electionProcessLabel.getTitle(), is(finishTextAndColor.text()));
                assertThat(electionProcessLabel.getColor(), is(finishTextAndColor.color()));
            }
        }

        private void assertIsNotFinishedResponse(
            LeaderElectionProcessResponse electionProcess
        ) {
            final LeaderElectionProcessDetailResponse electionProcessDetail = electionProcess.getDetail();
            final LeaderElectionProcessLabelResponse electionProcessLabel = electionProcess.getLabel();

            assertThat(electionProcessDetail, nullValue());
            assertThat(electionProcessLabel, nullValue());
        }

        private void assertVoteResponse(
            SolidarityLeaderElection solidarityLeaderElection,
            LeaderElectionProcessResponse leaderElectionProcessResponse,
            TextAndColor votePeriodTextAndColor
        ) {
            final LeaderElectionProcessDetailResponse electionProcessDetail = leaderElectionProcessResponse.getDetail();
            final LeaderElectionProcessLabelResponse electionProcessLabel = leaderElectionProcessResponse.getLabel();

            assertTime(electionProcessDetail.getStartDate(), solidarityLeaderElection.getVoteStartDateTime());
            if (solidarityLeaderElection.getElectionStatusDetails().isFinishedEarlyStatus()) {
                assertTime(electionProcessDetail.getEndDate(), solidarityLeaderElection.getVoteClosingDateTime());
                assertThat(electionProcessDetail.getEndDate(), notNullValue());
            } else {
                assertTime(electionProcessDetail.getEndDate(), solidarityLeaderElection.getVoteEndDateTime().minusSeconds(1));
            }
            assertThat(electionProcessDetail.getTitle(), nullValue());
            assertThat(electionProcessDetail.getValue(), nullValue());
            assertThat(electionProcessDetail.getUnit(), nullValue());

            if (votePeriodTextAndColor.isNull()) {
                assertThat(electionProcessLabel, nullValue());
            } else {
                assertThat(electionProcessLabel.getTitle(), is(votePeriodTextAndColor.text()));
                assertThat(electionProcessLabel.getColor(), is(votePeriodTextAndColor.color()));
            }
        }

        private void assertCandidateResponse(
            SolidarityLeaderElection solidarityLeaderElection,
            LeaderElectionProcessResponse leaderElectionProcessResponse,
            SolidarityLeaderElectionStatus electionStatus,
            TextAndColor candidatePeriodTextAndColor
        ) {
            final LeaderElectionProcessDetailResponse electionProcessDetail = leaderElectionProcessResponse.getDetail();
            final LeaderElectionProcessLabelResponse electionProcessLabel = leaderElectionProcessResponse.getLabel();

            assertThat(leaderElectionProcessResponse.getTitle(), is(electionStatus.getDisplayName()));
            assertTime(electionProcessDetail.getStartDate(), solidarityLeaderElection.getCandidateRegistrationStartDateTime());
            assertTime(electionProcessDetail.getEndDate(), solidarityLeaderElection.getCandidateRegistrationEndDateTime().minusSeconds(1));
            assertThat(electionProcessDetail.getTitle(), is("지원자 현황"));
            assertThat(electionProcessDetail.getValue(), is((long) candidateCount));
            assertThat(electionProcessDetail.getUnit(), is("명"));
            assertThat(electionProcessLabel.getTitle(), is(candidatePeriodTextAndColor.text()));
            assertThat(electionProcessLabel.getColor(), is(candidatePeriodTextAndColor.color()));
        }
    }

    @NotNull
    private LocalDateTime getTodayLocalDateTimeWithMocking() {
        final Integer hour = someIntegerBetween(0, 14);
        final Integer minute = someIntegerBetween(1, 59);

        LocalDateTime todayLocalDateTime = DateTimeUtil.getTodayLocalDateTime().withHour(hour).withMinute(minute);

        given(LeaderElectionCurrentDateTimeProvider.get()).willReturn(todayLocalDateTime);
        given(LeaderElectionCurrentDateTimeProvider.getKoreanDateTime())
            .willReturn(
                KoreanDateTimeUtil.getNowInKoreanTime()
                    .minusHours(KOREAN_TIME_OFFSET)
                    .withHour(hour)
                    .withMinute(minute)
            );

        return todayLocalDateTime;
    }

    private SolidarityLeaderElection createElectionAndGet(LocalDateTime dateTime, SolidarityLeaderElectionStatusGroup statusGroup) {
        final LocalDateTime candidateStartDateTime = dateTime.minusDays(
            someIntegerBetween(1, SolidarityLeaderElectionProcedure.RECRUIT_APPLICANTS.getDurationDays())
        );
        return itUtil.createSolidarityElection(stock.getCode(), statusGroup, candidateStartDateTime);
    }

    private SolidarityLeaderElection createElectionAndGet(
        LocalDateTime todayLocalDateTime,
        SolidarityLeaderElectionStatusGroup candidateRegisterStatusGroup,
        int candidateCount,
        SolidarityLeaderElectionPeriod electionPeriod
    ) {
        return createElectionAndGet(
            todayLocalDateTime,
            candidateRegisterStatusGroup,
            candidateCount,
            electionPeriod,
            null
        );
    }

    private SolidarityLeaderElection createElectionAndGet(
        LocalDateTime todayLocalDateTime,
        SolidarityLeaderElectionStatusGroup candidateRegisterStatusGroup,
        int candidateCount,
        SolidarityLeaderElectionPeriod electionPeriod,
        LocalDateTime voteClosingDateTime
    ) {
        final SolidarityLeaderElection solidarityLeaderElection = createElectionAndGet(todayLocalDateTime, candidateRegisterStatusGroup);

        solidarityLeaderElection.setCandidateCount(candidateCount);
        solidarityLeaderElection.setCandidateRegistrationStartDateTime(electionPeriod.candidateRegistrationStartDateTime());
        solidarityLeaderElection.setCandidateRegistrationEndDateTime(electionPeriod.candidateRegistrationEndDateTime());
        solidarityLeaderElection.setVoteStartDateTime(electionPeriod.voteStartDateTime());
        solidarityLeaderElection.setVoteEndDateTime(electionPeriod.voteEndDateTime());
        if (voteClosingDateTime != null) {
            solidarityLeaderElection.setVoteClosingDateTime(voteClosingDateTime);
        }

        return itUtil.updateSolidarityElection(solidarityLeaderElection);
    }

    interface TextAndColor {
        String text();

        String color();

        default boolean isNull() {
            return text() == null && color() == null;
        }
    }

    record FinishTextAndColor(String text, String color) implements TextAndColor {
        public static TextAndColor of(String text, String color) {
            return new FinishTextAndColor(text, color);
        }

        public static TextAndColor nullValue() {
            return new FinishTextAndColor(null, null);
        }
    }

    record CandidateRegistrationTextAndColor(String text, String color) implements TextAndColor {
        public static TextAndColor of(String text, String color) {
            return new CandidateRegistrationTextAndColor(text, color);
        }

        @SuppressWarnings("unused")
        public static TextAndColor nullValue() {
            return new CandidateRegistrationTextAndColor(null, null);
        }
    }

    record VoteTextAndColor(String text, String color) implements TextAndColor {
        public static TextAndColor of(String text, String color) {
            return new VoteTextAndColor(text, color);
        }

        public static TextAndColor nullValue() {
            return new VoteTextAndColor(null, null);
        }
    }
}
