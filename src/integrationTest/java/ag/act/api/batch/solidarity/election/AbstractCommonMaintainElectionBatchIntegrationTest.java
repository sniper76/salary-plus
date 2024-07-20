package ag.act.api.batch.solidarity.election;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.converter.DateTimeConverter;
import ag.act.dto.solidarity.election.SolidarityLeaderElectionStatusGroup;
import ag.act.entity.BatchLog;
import ag.act.entity.Board;
import ag.act.entity.Push;
import ag.act.entity.Solidarity;
import ag.act.entity.SolidarityDailySummary;
import ag.act.entity.SolidarityLeaderApplicant;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.entity.solidarity.election.SolidarityLeaderElection;
import ag.act.entity.solidarity.election.SolidarityLeaderElectionPollItemMapping;
import ag.act.enums.AppPreferenceType;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.enums.SlackChannel;
import ag.act.enums.push.PushSendStatus;
import ag.act.enums.solidarity.election.SolidarityLeaderElectionApplyStatus;
import ag.act.model.SimpleStringResponse;
import ag.act.util.DateTimeUtil;
import ag.act.util.KoreanDateTimeUtil;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockedStatic;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static ag.act.TestUtil.assertTime;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomIntegers.someIntegerBetween;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@SuppressWarnings({"AbbreviationAsWordInName", "checkstyle:MemberName", "checkstyle:LineLength"})
abstract class AbstractCommonMaintainElectionBatchIntegrationTest extends AbstractCommonIntegrationTest {

    protected static final String TARGET_API = "/api/batch/maintenance/solidarity-leader-elections";
    protected static final String BATCH_NAME = "MAINTAIN_SOLIDARITY_LEADER_ELECTIONS";
    protected static final int batchPeriod = 1;
    protected static final int TOTAL_CANDIDATE_COUNT = 3;
    protected static final long USER_HOLDING_STOCK_QUANTITY = 100L;
    protected static final long STOCK_TOTAL_ISSUED_QUANTITY = 2000L;
    protected static final long SOLIDARITY_STOCK_TOTAL_ISSUED_QUANTITY = 1000L;

    protected List<MockedStatic<?>> statics;
    protected Map<String, Integer> request;
    protected String date;
    protected Stock stock;
    protected Solidarity solidarity;
    protected SolidarityLeaderApplicant solidarityLeaderApplicant1;
    protected SolidarityLeaderApplicant solidarityLeaderApplicant2;
    protected SolidarityLeaderApplicant solidarityLeaderApplicant3;
    protected Board board;
    protected Instant todayInstant;
    protected LocalDateTime todayLocalDateTime;
    protected LocalDateTime midnightNextDayLocalDateTime;
    protected static final int additionalPushTimeMinutes = 10;

    @AfterEach
    void commonTearDown() {
        statics.forEach(MockedStatic::close);
        itUtil.deleteBatchLog(BATCH_NAME);
    }

    @BeforeEach
    void commonSetUp() {
        statics = List.of(mockStatic(DateTimeUtil.class), mockStatic(KoreanDateTimeUtil.class));
        itUtil.init();
        dbCleaner.clean();

        updateAllElectionsToBeFinishedOnTime();

        request = Map.of("batchPeriod", batchPeriod);
        date = someString(5);

        stock = itUtil.createStock();
        stock.setTotalIssuedQuantity(STOCK_TOTAL_ISSUED_QUANTITY);
        stock = itUtil.updateStock(stock);
        solidarity = createSolidarityAndMostRecentDailySummary();

        board = itUtil.createBoard(stock, BoardGroup.ANALYSIS, BoardCategory.SOLIDARITY_LEADER_ELECTION);
        todayInstant = Instant.now();
        todayLocalDateTime = DateTimeConverter.convert(todayInstant);
        midnightNextDayLocalDateTime = todayLocalDateTime.plusDays(someIntegerBetween(1, 3));

        given(serverEnvironment.isProd()).willReturn(Boolean.TRUE);
        given(DateTimeUtil.getCurrentFormattedDateTime()).willReturn(date);
        given(DateTimeUtil.getCurrentDateTimeMinusHours(batchPeriod)).willReturn(LocalDateTime.now());
        given(DateTimeUtil.getTodayInstant()).willReturn(todayInstant);
        given(DateTimeUtil.getTodayLocalDateTime()).willReturn(todayLocalDateTime);
        given(KoreanDateTimeUtil.toKoreanTimeUntilMidnightNextDay(any(LocalDateTime.class))).willReturn(midnightNextDayLocalDateTime);
        given(appPreferenceCache.getValue(AppPreferenceType.ADDITIONAL_PUSH_TIME_MINUTES)).willReturn(additionalPushTimeMinutes);
    }

    private Solidarity createSolidarityAndMostRecentDailySummary() {
        Solidarity solidarity = itUtil.createSolidarity(stock.getCode());
        solidarity.setMostRecentDailySummary(createSolidarityDailySummary());
        return itUtil.updateSolidarity(solidarity);
    }

    private SolidarityDailySummary createSolidarityDailySummary() {
        SolidarityDailySummary solidarityDailySummary = itUtil.createSolidarityDailySummary();
        solidarityDailySummary.setStockQuantity(SOLIDARITY_STOCK_TOTAL_ISSUED_QUANTITY);
        return itUtil.updateSolidarityDailySummary(solidarityDailySummary);
    }

    protected void assertElectionCandidateRegistrationStartPush() {
        final SolidarityLeaderElection solidarityLeaderElection = getSolidarityLeaderElection();
        final List<Push> pushes = itUtil.findAllPushesByPostIdAndSendStatus(solidarityLeaderElection.getPostId(), PushSendStatus.READY);

        assertThat(pushes.size(), is(1));
        assertThat(pushes.get(0).getTitle(), is("%s 주주대표 선출 절차".formatted(stock.getName())));
        assertThat(pushes.get(0).getContent(), is("%s 주주대표 선출 절차가 시작되었습니다. 후보자들의 공약을 살펴보고, 주주대표에 관심 있다면 직접 지원해보세요.".formatted(stock.getName())));
        assertTime(pushes.get(0).getTargetDatetime(), todayInstant.plusSeconds(additionalPushTimeMinutes * 60));
    }

    protected void assertElectionVoteStartPush() {
        final SolidarityLeaderElection solidarityLeaderElection = getSolidarityLeaderElection();
        final List<Push> pushes = itUtil.findAllPushesByPostIdAndSendStatus(solidarityLeaderElection.getPostId(), PushSendStatus.READY);

        assertThat(pushes.size(), is(1));
        assertThat(pushes.get(0).getTitle(), is("%s 주주대표 선출 절차".formatted(stock.getName())));
        assertThat(pushes.get(0).getContent(), is("%s 주주대표 선출 투표가 시작되었습니다. 내가 원하는 주주대표에 투표를 해 주세요!".formatted(stock.getName())));
        assertTime(pushes.get(0).getTargetDatetime(), todayInstant.plusSeconds(additionalPushTimeMinutes * 60));
    }

    protected void assertElectionFinishedPush() {
        final SolidarityLeaderElection solidarityLeaderElection = getSolidarityLeaderElection();
        final List<Push> pushes = itUtil.findAllPushesByPostIdAndSendStatus(solidarityLeaderElection.getPostId(), PushSendStatus.READY);

        assertThat(pushes.size(), is(1));
        assertThat(pushes.get(0).getTitle(), is("%s 주주대표 선출 투표 종료".formatted(stock.getName())));
        assertThat(pushes.get(0).getContent(), is("%s 주주대표 선출 투표가 종료되었습니다. 지금 바로 결과를 확인해 보세요!".formatted(stock.getName())));
        assertTime(pushes.get(0).getTargetDatetime(), todayInstant.plusSeconds(additionalPushTimeMinutes * 60));
    }

    protected void assertNoPushSent() {
        final SolidarityLeaderElection solidarityLeaderElection = getSolidarityLeaderElection();
        final List<Push> pushes = itUtil.findAllPushesByPostId(solidarityLeaderElection.getPostId());

        assertThat(pushes.size(), is(0));
    }

    protected void assertElectionVoteStartSlackMessage() {
        then(slackMessageSender).should()
            .sendSlackMessage("%s 주주대표 선출 투표 시작".formatted(stock.getName()), SlackChannel.ACT_SOLIDARITY_LEADER_APPLICANT_ALERT);
    }

    protected void assertNoSlackMessage() {
        then(slackMessageSender).should(never())
            .sendSlackMessage(anyString(), any(SlackChannel.class));
    }

    protected void assertElectionFinishedSlackMessage() {
        then(slackMessageSender).should()
            .sendSlackMessage("%s 주주대표 선출 투표 종료".formatted(stock.getName()), SlackChannel.ACT_SOLIDARITY_LEADER_APPLICANT_ALERT);
    }

    protected void assertResponse(SimpleStringResponse result) {
        final String expectedResult = "[Batch] %s batch successfully finished. [finished: %s / %s] on %s"
            .formatted(BATCH_NAME, 1, 1, date);

        assertThat(result.getStatus(), is(expectedResult));
        assertBatchLog(expectedResult);
    }

    protected void assertTotalStockQuantity() {
        final SolidarityLeaderElection solidarityLeaderElection = getSolidarityLeaderElection();

        assertThat(solidarityLeaderElection.getTotalStockQuantity(), is(SOLIDARITY_STOCK_TOTAL_ISSUED_QUANTITY));
    }

    protected void assertCandidateCount() {
        final SolidarityLeaderElection solidarityLeaderElection = getSolidarityLeaderElection();

        assertThat(solidarityLeaderElection.getCandidateCount(), is(TOTAL_CANDIDATE_COUNT));
    }

    protected void assertBatchLog(String expectedResult) {
        final BatchLog batchLog = itUtil.findLastBatchLog(BATCH_NAME).orElseThrow();

        assertThat(batchLog.getResult(), is(expectedResult));
    }

    protected SimpleStringResponse callApiAndGetResult() throws Exception {
        final MvcResult response = callApi(status().isOk());

        return itUtil.getResult(response, SimpleStringResponse.class);
    }

    @NotNull
    protected MvcResult callApi(ResultMatcher resultMatcher) throws Exception {
        return mockMvc
            .perform(
                post(TARGET_API)
                    .content(objectMapperUtil.toJson(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("x-api-key", "b0e6f688a1a08462201ef69f4")
            )
            .andExpect(resultMatcher)
            .andReturn();
    }

    protected List<SolidarityLeaderElectionPollItemMapping> getPollItemMappings(SolidarityLeaderElection solidarityLeaderElection) {
        return itUtil.findAllSolidarityLeaderElectionPollItemMappingBySolidarityLeaderElectionId(solidarityLeaderElection.getId());
    }

    protected void updateAllElectionsToBeFinishedOnTime() {
        itUtil.findAllSolidarityLeaderElections()
            .stream()
            .peek(it -> {
                final LocalDateTime currentDateTime = LocalDateTime.now();
                it.setCandidateRegistrationStartDateTime(currentDateTime.minusDays(100));
                it.setCandidateRegistrationEndDateTime(currentDateTime.minusDays(90));
                it.setVoteStartDateTime(currentDateTime.minusDays(90));
                it.setVoteEndDateTime(currentDateTime.minusDays(80));
                it.setVoteClosingDateTime(it.getVoteEndDateTime());
                it.setElectionStatusGroup(SolidarityLeaderElectionStatusGroup.FINISHED_ON_TIME_STATUS_GROUP);
            })
            .forEach(itUtil::updateSolidarityElection);
    }

    protected SolidarityLeaderApplicant createSolidarityLeaderApplicant(SolidarityLeaderElection solidarityElection) {
        return createSolidarityLeaderApplicant(solidarityElection, SolidarityLeaderElectionApplyStatus.COMPLETE);
    }

    protected SolidarityLeaderApplicant createSolidarityLeaderApplicant(SolidarityLeaderElection solidarityElection, SolidarityLeaderElectionApplyStatus applyStatus) {
        final User user = createUserAndHoldingStock();

        return itUtil.createSolidarityLeaderApplicant(
            solidarity.getId(),
            user.getId(),
            applyStatus,
            solidarityElection.getId()
        );
    }

    protected User createUserAndHoldingStock() {
        final User user = itUtil.createUser();
        itUtil.createUserHoldingStock(solidarity.getStockCode(), user, USER_HOLDING_STOCK_QUANTITY);
        return user;
    }

    protected SolidarityLeaderElection getSolidarityLeaderElection() {
        return itUtil.findSolidarityLeaderElectionByStockCode(stock.getCode()).orElseThrow();
    }
}
