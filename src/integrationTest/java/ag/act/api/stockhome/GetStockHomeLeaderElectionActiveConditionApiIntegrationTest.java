package ag.act.api.stockhome;

import ag.act.dto.solidarity.election.SolidarityLeaderElectionStatusGroup;
import ag.act.entity.Solidarity;
import ag.act.entity.SolidarityDailySummary;
import ag.act.entity.solidarity.election.SolidarityLeaderElection;
import ag.act.enums.AppPreferenceType;
import ag.act.enums.solidarity.election.SolidarityLeaderElectionStatus;
import ag.act.enums.solidarity.election.SolidarityLeaderElectionStatusDetails;
import ag.act.model.LeaderElectionFeatureActiveConditionResponse;
import ag.act.model.StockHomeResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static ag.act.TestUtil.someLocalDateTimeInTheFuture;
import static ag.act.TestUtil.someSolidarityLeaderElectionOngoingStatus;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomDoubles.someDoubleBetween;
import static shiver.me.timbers.data.random.RandomIntegers.someIntegerBetween;

class GetStockHomeLeaderElectionActiveConditionApiIntegrationTest extends GetStockHomeApiIntegrationTest {

    private final float minThresholdStake = 5f;
    private final long minThresholdMemberCount = 1000L;

    @BeforeEach
    void setUp() {
        given(appPreferenceCache.getValue(AppPreferenceType.LEADER_ELECTION_FEATURE_ACTIVE_MIN_THRESHOLD_STAKE))
            .willReturn(minThresholdStake);
        given(appPreferenceCache.getValue(AppPreferenceType.LEADER_ELECTION_FEATURE_ACTIVE_MIN_THRESHOLD_MEMBER_COUNT))
            .willReturn(minThresholdMemberCount);
    }

    private void assertLeaderElectionFeatureActiveCondition(
        LeaderElectionFeatureActiveConditionResponse leaderElectionFeatureActiveCondition,
        int memberCount,
        double stake,
        boolean isVisible
    ) {
        assertThat(leaderElectionFeatureActiveCondition.getMinThresholdStake(), is(minThresholdStake));
        assertThat(leaderElectionFeatureActiveCondition.getStake(), is((float) stake));
        assertThat(leaderElectionFeatureActiveCondition.getMinThresholdMemberCount(), is(minThresholdMemberCount));
        assertThat(leaderElectionFeatureActiveCondition.getMemberCount().intValue(), is(memberCount));
        assertThat(leaderElectionFeatureActiveCondition.getIsVisible(), is(isVisible));
    }


    private void mockMostRecentSolidarityDailySummary(
        Solidarity toBeUpdatedSolidarity, Integer memberCount, Double stake
    ) {
        final SolidarityDailySummary summary = mockSolidarityDailySummary(memberCount, stake);
        itUtil.updateSolidarityDailySummary(summary);
        toBeUpdatedSolidarity.setMostRecentDailySummary(summary);
        itUtil.updateSolidarity(toBeUpdatedSolidarity);
    }

    private SolidarityDailySummary mockSolidarityDailySummary(Integer memberCount, Double stake) {
        final SolidarityDailySummary summary = itUtil.createSolidarityDailySummary();
        summary.setMemberCount(memberCount);
        summary.setStake(stake);

        return summary;
    }

    @Nested
    class WhenMostRecentDailySummaryNonExists {

        final int memberCount = 0;
        final double stake = 0.0;
        final boolean isVisible = false;

        @BeforeEach
        void setUp() {
            solidarity.setMostRecentDailySummary(null);
        }

        @Test
        void shouldReturnLeaderElectionFeatureActiveCondition() throws Exception {
            StockHomeResponse result = getResponse(callApi(status().isOk()));

            LeaderElectionFeatureActiveConditionResponse electionFeatureActiveCondition = result.getLeaderElectionFeatureActiveCondition();
            assertLeaderElectionFeatureActiveCondition(electionFeatureActiveCondition, memberCount, stake, isVisible);
        }
    }

    @Nested
    class WhenMostRecentDailySummaryExists {

        @Nested
        class AndSolidarityHasEverHadLeader {

            final int memberCount = someMemberCountUnderMinThreshold();
            final double stake = someMemberCountUnderMinThreshold();
            final boolean isVisible = false;

            @BeforeEach
            void setUp() {
                mockMostRecentSolidarityDailySummary(
                    solidarity, memberCount, stake
                );

                solidarity.setHasEverHadLeader(true);
                itUtil.updateSolidarity(solidarity);
            }

            @Test
            void shouldReturnLeaderElectionFeatureActiveConditionResponse() throws Exception {
                StockHomeResponse result = getResponse(callApi(status().isOk()));

                LeaderElectionFeatureActiveConditionResponse electionFeatureActiveCondition = result.getLeaderElectionFeatureActiveCondition();
                assertLeaderElectionFeatureActiveCondition(electionFeatureActiveCondition, memberCount, stake, isVisible);
            }
        }

        @Nested
        class AndSolidarityHasOngoingElection {

            final int memberCount = someMemberCountUnderMinThreshold();
            final double stake = someMemberCountUnderMinThreshold();
            final boolean isVisible = false;

            @BeforeEach
            void setUp() {
                mockMostRecentSolidarityDailySummary(
                    solidarity, memberCount, stake
                );

                solidarity.setHasEverHadLeader(false);
                itUtil.updateSolidarity(solidarity);

                final SolidarityLeaderElection solidarityLeaderElection = itUtil.createSolidarityElection(
                    stock.getCode(), new SolidarityLeaderElectionStatusGroup(
                        someSolidarityLeaderElectionOngoingStatus(),
                        SolidarityLeaderElectionStatusDetails.IN_PROGRESS
                    ));
                final LocalDateTime localDateTime = someLocalDateTimeInTheFuture();
                solidarityLeaderElection.setCandidateRegistrationStartDateTime(localDateTime);
                solidarityLeaderElection.setCandidateRegistrationEndDateTime(localDateTime.plusDays(3));
                solidarityLeaderElection.setVoteStartDateTime(localDateTime.plusDays(4));
                solidarityLeaderElection.setVoteEndDateTime(localDateTime.plusDays(6));
                itUtil.updateSolidarityElection(solidarityLeaderElection);
            }

            @Test
            void shouldReturnLeaderElectionFeatureActiveConditionResponse() throws Exception {
                StockHomeResponse result = getResponse(callApi(status().isOk()));

                LeaderElectionFeatureActiveConditionResponse electionFeatureActiveCondition = result.getLeaderElectionFeatureActiveCondition();
                assertLeaderElectionFeatureActiveCondition(electionFeatureActiveCondition, memberCount, stake, isVisible);
            }
        }

        @Nested
        class AndLeaderElectionFeatureActiveConditionIsSatisfied {

            @Nested
            class AndOnlyStakeOverMinThreshold {
                final int memberCount = someMemberCountUnderMinThreshold();
                final double stake = someStakeOverMinThreshold();
                final boolean isVisible = false;

                @BeforeEach
                void setUp() {
                    mockMostRecentSolidarityDailySummary(
                        solidarity, memberCount, stake
                    );

                    solidarity.setHasEverHadLeader(false);
                    itUtil.updateSolidarity(solidarity);
                }

                @Test
                void shouldReturnLeaderElectionFeatureActiveConditionResponse() throws Exception {
                    StockHomeResponse result = getResponse(callApi(status().isOk()));

                    LeaderElectionFeatureActiveConditionResponse electionFeatureActiveCondition =
                        result.getLeaderElectionFeatureActiveCondition();
                    assertLeaderElectionFeatureActiveCondition(electionFeatureActiveCondition, memberCount, stake, isVisible);
                }
            }

            @Nested
            class AndOnlyMemberCountOverMinThreshold {
                final int memberCount = someMemberCountOverMinThreshold();
                final double stake = someStakeUnderMinThreshold();
                final boolean isVisible = false;

                @BeforeEach
                void setUp() {
                    mockMostRecentSolidarityDailySummary(
                        solidarity, memberCount, stake
                    );

                    solidarity.setHasEverHadLeader(false);
                    itUtil.updateSolidarity(solidarity);
                }

                @Test
                void shouldReturnLeaderElectionFeatureActiveConditionResponse() throws Exception {
                    StockHomeResponse result = getResponse(callApi(status().isOk()));

                    LeaderElectionFeatureActiveConditionResponse electionFeatureActiveCondition =
                        result.getLeaderElectionFeatureActiveCondition();
                    assertLeaderElectionFeatureActiveCondition(electionFeatureActiveCondition, memberCount, stake, isVisible);
                }
            }
        }

        @Nested
        class AndLeaderElectionFeatureActiveConditionIsNotSatisfied {
            final int memberCount = someMemberCountUnderMinThreshold();
            final double stake = someStakeUnderMinThreshold();
            final boolean isVisible = true;

            @BeforeEach
            void setUp() {
                mockMostRecentSolidarityDailySummary(
                    solidarity, memberCount, stake
                );

                solidarity.setHasEverHadLeader(false);
                itUtil.updateSolidarity(solidarity);
            }

            @Nested
            class AndSolidarityDoesNotHaveLeaderElection {

                @Test
                void shouldReturnLeaderElectionFeatureActiveConditionResponse() throws Exception {
                    StockHomeResponse result = getResponse(callApi(status().isOk()));

                    LeaderElectionFeatureActiveConditionResponse electionFeatureActiveCondition =
                        result.getLeaderElectionFeatureActiveCondition();
                    assertLeaderElectionFeatureActiveCondition(electionFeatureActiveCondition, memberCount, stake, isVisible);
                }
            }

            @Nested
            class AndSolidarityHasRegistrationPendingElection {

                @BeforeEach
                void setUp() {
                    final SolidarityLeaderElection solidarityLeaderElection = itUtil.createSolidarityElection(
                        stock.getCode(), new SolidarityLeaderElectionStatusGroup(
                            SolidarityLeaderElectionStatus.CANDIDATE_REGISTRATION_PENDING_PERIOD,
                            SolidarityLeaderElectionStatusDetails.IN_PROGRESS
                        ));
                    if (solidarityLeaderElection.getElectionStatus() == SolidarityLeaderElectionStatus.CANDIDATE_REGISTRATION_PERIOD) {
                        final LocalDateTime localDateTime = someLocalDateTimeInTheFuture();
                        solidarityLeaderElection.setCandidateRegistrationStartDateTime(localDateTime);
                        solidarityLeaderElection.setCandidateRegistrationEndDateTime(localDateTime.plusDays(3));
                        solidarityLeaderElection.setVoteStartDateTime(localDateTime.plusDays(4));
                        solidarityLeaderElection.setVoteEndDateTime(localDateTime.plusDays(6));
                        itUtil.updateSolidarityElection(solidarityLeaderElection);
                    }
                }

                @Test
                void shouldReturnLeaderElectionFeatureActiveConditionResponse() throws Exception {
                    StockHomeResponse result = getResponse(callApi(status().isOk()));

                    LeaderElectionFeatureActiveConditionResponse electionFeatureActiveCondition =
                        result.getLeaderElectionFeatureActiveCondition();
                    assertLeaderElectionFeatureActiveCondition(electionFeatureActiveCondition, memberCount, stake, isVisible);
                }
            }
        }
    }

    private int someMemberCountOverMinThreshold() {
        return (int) minThresholdMemberCount + someIntegerBetween(0, 1000);
    }

    private double someStakeOverMinThreshold() {
        return minThresholdStake + someDoubleBetween(0.0, 100.0 - minThresholdStake);
    }

    private int someMemberCountUnderMinThreshold() {
        return (int) minThresholdMemberCount - someIntegerBetween(0, (int) minThresholdMemberCount);
    }

    private double someStakeUnderMinThreshold() {
        return (double) minThresholdStake - someDoubleBetween(0.0, (double) minThresholdStake);
    }
}
