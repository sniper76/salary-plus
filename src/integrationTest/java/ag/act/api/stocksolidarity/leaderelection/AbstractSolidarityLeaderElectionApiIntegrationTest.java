package ag.act.api.stocksolidarity.leaderelection;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Solidarity;
import ag.act.entity.SolidarityDailySummary;
import ag.act.entity.SolidarityLeaderApplicant;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.entity.solidarity.election.SolidarityLeaderElection;
import ag.act.enums.SlackChannel;
import ag.act.enums.SolidarityLeaderElectionProcedure;
import ag.act.enums.solidarity.election.SolidarityLeaderElectionApplyStatus;
import ag.act.enums.solidarity.election.SolidarityLeaderElectionStatus;
import ag.act.exception.BadRequestException;
import ag.act.util.KoreanDateTimeUtil;
import ag.act.util.SolidarityLeaderElectionFeatureActiveConditionProvider;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;

import java.time.LocalDateTime;

import static ag.act.TestUtil.assertTime;
import static ag.act.TestUtil.someSolidarityLeaderElectionApplicationItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static shiver.me.timbers.data.random.RandomDoubles.someDoubleBetween;
import static shiver.me.timbers.data.random.RandomIntegers.someIntegerBetween;

abstract class AbstractSolidarityLeaderElectionApiIntegrationTest extends AbstractCommonIntegrationTest {

    protected static final int CANDIDATE_DURATION_DAYS = SolidarityLeaderElectionProcedure.RECRUIT_APPLICANTS.getDurationDays();
    protected static final int VOTE_DURATION_DAYS = SolidarityLeaderElectionProcedure.START_VOTING.getDurationDays();

    protected Stock stock;
    protected Solidarity solidarity;
    protected User user;
    protected String jwt;
    protected ag.act.model.SolidarityLeaderElectionApplyRequest request;
    protected SolidarityDailySummary solidarityDailySummary;
    protected Float minThresholdStake;
    protected Long minThresholdMemberCount;
    protected int candidateCount;

    @Mock
    private SolidarityLeaderElectionFeatureActiveConditionProvider solidarityLeaderElectionFeatureActiveConditionProvider;


    @BeforeEach
    void setUp() {
        itUtil.init();
        itUtil.deleteAllSolidarityLeaderElections();

        stock = itUtil.createStock();
        solidarity = itUtil.createSolidarity(stock.getCode());
        user = itUtil.createUser();
        jwt = itUtil.createJwt(user.getId());

        itUtil.createUserHoldingStock(stock.getCode(), user);

        minThresholdStake = 5.0f;
        minThresholdMemberCount = 1000L;
        solidarityDailySummary = createSolidarityDailySummary();
        mappingSolidarityAndSolidarityDailySummary(solidarity, solidarityDailySummary);

        given(solidarityLeaderElectionFeatureActiveConditionProvider.getMinThresholdStake())
            .willReturn(minThresholdStake);
        given(solidarityLeaderElectionFeatureActiveConditionProvider.getMinThresholdMemberCount())
            .willReturn(minThresholdMemberCount);
    }

    protected void mappingSolidarityAndSolidarityDailySummary(Solidarity solidarity, SolidarityDailySummary solidarityDailySummary) {
        solidarity.setMostRecentDailySummary(solidarityDailySummary);
        itUtil.updateSolidarity(solidarity);
    }

    private SolidarityDailySummary createSolidarityDailySummary() {
        SolidarityDailySummary solidarityDailySummary = itUtil.createSolidarityDailySummary();
        solidarityDailySummary.setStake(
            someDoubleBetween((double) minThresholdStake, 100 - (double) minThresholdStake)
        );
        solidarityDailySummary.setMemberCount(
            someIntegerBetween(minThresholdMemberCount.intValue(), minThresholdMemberCount.intValue() + 10000)
        );
        return itUtil.updateSolidarityDailySummary(solidarityDailySummary);
    }

    protected SolidarityLeaderElection getSolidarityLeaderElectionByStockCode() {
        return itUtil.findSolidarityLeaderElectionByStockCode(stock.getCode())
            .orElseThrow(() -> new BadRequestException("[TEST] 선출 정보가 없습니다."));
    }

    protected SolidarityLeaderApplicant getSolidarityLeaderApplicantBySolidarityIdAndUserId() {
        return itUtil.findSolidarityLeaderApplicant(solidarity.getId(), user.getId())
            .orElseThrow(() -> new BadRequestException("[TEST] 지원 정보가 없습니다."));
    }

    protected ag.act.model.SolidarityLeaderElectionApplyRequest genRequest(String applicationItem, SolidarityLeaderElectionApplyStatus status) {
        return new ag.act.model.SolidarityLeaderElectionApplyRequest()
            .reasonsForApply(someSolidarityLeaderElectionApplicationItem())
            .knowledgeOfCompanyManagement(someSolidarityLeaderElectionApplicationItem())
            .goals(someSolidarityLeaderElectionApplicationItem())
            .commentsForStockHolder(applicationItem)
            .applyStatus(status.name());
    }

    protected void assertSolidarityLeaderElectionIsPending() {
        SolidarityLeaderElection election = itUtil.findSolidarityLeaderElectionByStockCode(stock.getCode()).orElseThrow();
        assertThat(election.isPendingElection(), is(true));
        assertThat(election.getCandidateCount(), is(0));
    }

    protected void assertNotSendSlackMessage() {
        then(slackMessageSender).should(never())
            .sendSlackMessage(anyString(), any(SlackChannel.class));
    }

    protected void assertSendSlackMessage() {
        then(slackMessageSender).should()
            .sendSlackMessage("%s 주주대표 후보자 %s 지원".formatted(stock.getName(), user.getNickname()),
                SlackChannel.ACT_SOLIDARITY_LEADER_APPLICANT_ALERT);
    }

    protected void assertSolidarityLeaderElection(
        SolidarityLeaderElection election,
        LocalDateTime currentKoreanDateTime,
        LocalDateTime expectedCandidateRegistrationStartDateTime
    ) {
        final LocalDateTime expectedCandidateRegistrationEndUtcDateTime = getExpectedCandidateRegistrationEndUtcDateTime(currentKoreanDateTime);
        final LocalDateTime expectedVoteStartDateTime = expectedCandidateRegistrationEndUtcDateTime;
        final LocalDateTime expectedVoteEndDateTime = expectedVoteStartDateTime.plusDays(VOTE_DURATION_DAYS);

        assertThat(election.getStockCode(), is(stock.getCode()));
        assertThat(election.getCandidateCount(), is(candidateCount));
        assertThat(election.getElectionStatus(), is(SolidarityLeaderElectionStatus.CANDIDATE_REGISTRATION_PERIOD));
        assertTime(election.getCandidateRegistrationStartDateTime(), expectedCandidateRegistrationStartDateTime);
        assertTime(election.getCandidateRegistrationEndDateTime(), expectedCandidateRegistrationEndUtcDateTime);
        assertTime(election.getVoteStartDateTime(), expectedVoteStartDateTime);
        assertTime(election.getVoteEndDateTime(), expectedVoteEndDateTime);

        assertTimeEndsWith(election.getCandidateRegistrationEndDateTime());
        assertTimeEndsWith(election.getVoteStartDateTime());
        assertTimeEndsWith(election.getVoteEndDateTime());
    }

    private void assertTimeEndsWith(LocalDateTime localDateTime) {
        assertThat(localDateTime.getHour(), is(15));
        assertThat(localDateTime.getMinute(), is(0));
        assertThat(localDateTime.getSecond(), is(0));
    }

    private LocalDateTime getExpectedCandidateRegistrationEndUtcDateTime(LocalDateTime currentKoreanDateTime) {
        return KoreanDateTimeUtil.toUtcDateTimeFromKoreanLocalDate(currentKoreanDateTime.toLocalDate())
            .plusDays(CANDIDATE_DURATION_DAYS);
    }
}
