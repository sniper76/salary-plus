package ag.act.facade.batch.executor;

import ag.act.dto.BatchParameter;
import ag.act.entity.BatchLog;
import ag.act.facade.batch.BatchRunner;
import ag.act.service.BatchLogService;
import ag.act.util.DateTimeUtil;
import jakarta.persistence.EntityManager;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willCallRealMethod;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.mockStatic;
import static shiver.me.timbers.data.random.RandomIntegers.someIntegerBetween;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class CleanupUnfinishedBatchesBatchExecutorTest {
    @InjectMocks
    private CleanupUnfinishedBatchesBatchExecutor batch;
    private List<MockedStatic<?>> statics;
    @Mock
    private BatchLogService batchLogService;
    @Mock
    private BatchRunner batchRunner;
    @Mock
    private EntityManager entityManager;
    @Mock
    private BatchParameter batchParameter;
    private String date;
    private String batchName;
    private String actualMessage;

    @AfterEach
    void tearDown() {
        statics.forEach(MockedStatic::close);
    }

    @BeforeEach
    void setUp() {
        statics = List.of(mockStatic(DateTimeUtil.class));
        batchName = someString(20);
        date = someString(5);

        willCallRealMethod().given(batchRunner).run(any(), any());
        willDoNothing().given(entityManager).flush();
        willDoNothing().given(entityManager).clear();
    }

    @Nested
    class CleanupUnfinishedBatches {

        @Mock
        private BatchLog batchLog1;
        @Mock
        private BatchLog batchLog2;
        private int successCount;

        @BeforeEach
        void setUp() {
            successCount = 2;
            final List<BatchLog> allBatchLogs = List.of(batchLog1, batchLog2);

            given(DateTimeUtil.getCurrentFormattedDateTime()).willReturn(date);
            given(batchParameter.getBatchPeriod()).willReturn(someIntegerBetween(1, 100));
            given(batchParameter.getBatchName()).willReturn(batchName);
            given(batchLogService.getUnfinishedBatchesForCleanup()).willReturn(allBatchLogs);

            willDoNothing().given(batchLogService).forceToFailBatchLog(batchLog1);
            willDoNothing().given(batchLogService).forceToFailBatchLog(batchLog2);

            actualMessage = batch.execute(batchParameter);
        }

        @Test
        void shouldReturnResultMessage() {
            assertThat(actualMessage, Matchers.is(
                "[Batch] %s batch successfully finished. [finished: %s / %s]"
                    .formatted(batchName, successCount, 2)
            ));
        }

        @Test
        void shouldCallGetBatchLogsForCleanup() {
            then(batchLogService).should().getUnfinishedBatchesForCleanup();
        }

        @Test
        void shouldForceToFailBatchLogs() {
            then(batchLogService).should().forceToFailBatchLog(batchLog1);
            then(batchLogService).should().forceToFailBatchLog(batchLog2);
        }
    }
}
