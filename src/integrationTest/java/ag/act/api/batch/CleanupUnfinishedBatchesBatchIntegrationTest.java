package ag.act.api.batch;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.BatchLog;
import ag.act.enums.BatchStatus;
import ag.act.util.DateTimeUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static ag.act.TestUtil.assertTime;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomEnums.someEnum;
import static shiver.me.timbers.data.random.RandomIntegers.someIntegerBetween;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;
import static shiver.me.timbers.data.random.RandomStrings.someString;

public class CleanupUnfinishedBatchesBatchIntegrationTest extends AbstractCommonIntegrationTest {
    private static final String TARGET_API = "/api/batch/cleanup/unfinished-batches";
    private static final String BATCH_NAME = "CLEANUP_UNFINISHED_BATCHES";

    private List<MockedStatic<?>> statics;

    private Map<String, Integer> request;
    private List<BatchLog> noInProgressBatchLogs;
    private List<BatchLog> inProgressBatchLogsForCleanup;
    private List<BatchLog> recentlyStartedInProgressBatchLogs;

    @AfterEach
    void tearDown() {
        statics.forEach(MockedStatic::close);
    }

    @BeforeEach
    void setUp() {
        statics = List.of(mockStatic(DateTimeUtil.class));
        itUtil.init();

        noInProgressBatchLogs = List.of(
            createBatchLog(BatchStatus.START, LocalDateTime.now().minusMinutes(someIntegerBetween(11, 100))),
            createBatchLog(BatchStatus.START, LocalDateTime.now().minusMinutes(someIntegerBetween(11, 100))),
            createBatchLog(BatchStatus.FAILURE, LocalDateTime.now().minusMinutes(someIntegerBetween(11, 100))),
            createBatchLog(BatchStatus.FAILURE, LocalDateTime.now().minusMinutes(someIntegerBetween(11, 100))),
            createBatchLog(BatchStatus.SUCCESS, LocalDateTime.now().minusMinutes(someIntegerBetween(11, 100))),
            createBatchLog(BatchStatus.SUCCESS, LocalDateTime.now().minusMinutes(someIntegerBetween(11, 100)))
        );

        inProgressBatchLogsForCleanup = List.of(
            createBatchLog(BatchStatus.IN_PROGRESS, LocalDateTime.now().minusMinutes(11)),
            createBatchLog(BatchStatus.IN_PROGRESS, LocalDateTime.now().minusMinutes(someIntegerBetween(11, 1000))),
            createBatchLog(BatchStatus.IN_PROGRESS, LocalDateTime.now().minusMinutes(someIntegerBetween(11, 1000))),
            createBatchLog(BatchStatus.IN_PROGRESS, LocalDateTime.now().minusMinutes(someIntegerBetween(11, 1000))),
            createBatchLog(BatchStatus.IN_PROGRESS, LocalDateTime.now().minusMinutes(someIntegerBetween(11, 1000)))
        );

        recentlyStartedInProgressBatchLogs = List.of(
            createBatchLog(BatchStatus.IN_PROGRESS, LocalDateTime.now().minusMinutes(someIntegerBetween(0, 10))),
            createBatchLog(BatchStatus.IN_PROGRESS, LocalDateTime.now().minusMinutes(someIntegerBetween(0, 10))),
            createBatchLog(BatchStatus.IN_PROGRESS, LocalDateTime.now().minusMinutes(someIntegerBetween(0, 10))),
            createBatchLog(BatchStatus.IN_PROGRESS, LocalDateTime.now().minusMinutes(someIntegerBetween(0, 10))),
            createBatchLog(BatchStatus.IN_PROGRESS, LocalDateTime.now().minusMinutes(someIntegerBetween(0, 10)))
        );

        // Prepare Batch Data
        final int batchPeriod = 1;
        request = Map.of("batchPeriod", batchPeriod);

        given(DateTimeUtil.getCurrentFormattedDateTime()).willReturn(someString(5));
        given(DateTimeUtil.getCurrentDateTimeMinusHours(batchPeriod)).willReturn(LocalDateTime.now());
    }

    private BatchLog createBatchLog(BatchStatus batchStatus, LocalDateTime startTime) {
        final String batchName = someAlphanumericString(10);

        BatchLog batchLog = new BatchLog();
        batchLog.setBatchStatus(batchStatus);
        batchLog.setBatchName(batchName);
        batchLog.setPeriodTimeUnit(someEnum(ag.act.model.BatchRequest.PeriodTimeUnitEnum.class));
        batchLog.setBatchPeriod(someIntegerBetween(1, 100));
        batchLog.setBatchGroupName(batchName + "_" + someAlphanumericString(10));
        batchLog.setStartTime(startTime);

        return itUtil.updateBatchLog(batchLog);
    }

    @DisplayName("Should return 200 response code when call " + TARGET_API)
    @Test
    void shouldCleanupTheUnfinishedBatchesThatAreStarted10minutesAgo() throws Exception {
        MvcResult response = mockMvc
            .perform(
                post(TARGET_API)
                    .content(objectMapperUtil.toJson(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("x-api-key", "b0e6f688a1a08462201ef69f4")
            )
            .andExpect(status().isOk())
            .andReturn();

        final ag.act.model.SimpleStringResponse result = objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            ag.act.model.SimpleStringResponse.class
        );

        assertResponse(result);
        assertBatchLogsWithNoChanges(noInProgressBatchLogs);
        assertBatchLogsWithNoChanges(recentlyStartedInProgressBatchLogs);
        assertFinishedBatchLogs(inProgressBatchLogsForCleanup);
    }

    private void assertResponse(ag.act.model.SimpleStringResponse result) {
        final String expectedResult = "[Batch] %s batch successfully finished. [finished: %s / %s]".formatted(BATCH_NAME, 5, 5);
        assertThat(result.getStatus(), is(expectedResult));
    }

    private void assertBatchLogsWithNoChanges(List<BatchLog> batchLogs) {
        batchLogs.forEach(batchLog -> {
            final BatchLog batchLogFromDatabase = itUtil.findBatchLogById(batchLog.getId()).orElseThrow();

            assertThat(batchLogFromDatabase.getBatchStatus(), is(batchLog.getBatchStatus()));
            assertThat(batchLogFromDatabase.getBatchName(), is(batchLog.getBatchName()));
            assertThat(batchLogFromDatabase.getBatchGroupName(), is(batchLog.getBatchGroupName()));
            assertThat(batchLogFromDatabase.getBatchPeriod(), is(batchLog.getBatchPeriod()));
            assertThat(batchLogFromDatabase.getPeriodTimeUnit(), is(batchLog.getPeriodTimeUnit()));
            assertThat(batchLogFromDatabase.getResult(), is(batchLog.getResult()));
            assertTime(batchLogFromDatabase.getStartTime(), batchLog.getStartTime());
            assertTime(batchLogFromDatabase.getEndTime(), batchLog.getEndTime());
            assertTime(batchLogFromDatabase.getCreatedAt(), batchLog.getCreatedAt());
            assertTime(batchLogFromDatabase.getUpdatedAt(), batchLog.getUpdatedAt());
        });
    }

    private void assertFinishedBatchLogs(List<BatchLog> batchLogs) {
        batchLogs.forEach(batchLog -> {
            final BatchLog batchLogFromDatabase = itUtil.findBatchLogById(batchLog.getId()).orElseThrow();

            assertThat(batchLogFromDatabase.getBatchStatus(), is(BatchStatus.FAILURE));
            assertThat(batchLogFromDatabase.getBatchName(), is(batchLog.getBatchName()));
            assertThat(batchLogFromDatabase.getBatchGroupName(), is(batchLog.getBatchGroupName()));
            assertThat(batchLogFromDatabase.getBatchPeriod(), is(batchLog.getBatchPeriod()));
            assertThat(batchLogFromDatabase.getPeriodTimeUnit(), is(batchLog.getPeriodTimeUnit()));
            assertThat(batchLogFromDatabase.getResult(), is("Forced to fail"));
            assertTime(batchLogFromDatabase.getStartTime(), batchLog.getStartTime());
            assertTime(batchLogFromDatabase.getEndTime(), greaterThan(batchLog.getStartTime()));
            assertTime(batchLogFromDatabase.getCreatedAt(), batchLog.getCreatedAt());
            assertTime(batchLogFromDatabase.getUpdatedAt(), greaterThan(batchLog.getUpdatedAt()));
        });
    }
}
