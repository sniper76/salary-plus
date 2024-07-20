package ag.act.facade.batch.executor;

import ag.act.dto.BatchParameter;
import ag.act.entity.Stock;
import ag.act.facade.batch.BatchRunner;
import ag.act.service.IBatchProcessor;
import ag.act.service.stock.UpdateStockBatchProcessor;
import ag.act.util.DateTimeUtil;
import jakarta.persistence.EntityManager;
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
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willCallRealMethod;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.mockStatic;
import static shiver.me.timbers.data.random.RandomIntegers.someIntegerBetween;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class UpdateStocksBatchExecutorTest {
    @InjectMocks
    private UpdateStocksBatchExecutor batch;
    private List<MockedStatic<?>> statics;
    @Mock
    private UpdateStockBatchProcessor updateStockBatchProcessor;
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
    class UpdateStocks {

        private List<Stock> sourceStocks;

        @BeforeEach
        void setUp() {
            sourceStocks = List.of();

            given(DateTimeUtil.getLatestStockMarketClosingDate()).willReturn(date);
            given(batchParameter.getBatchPeriod()).willReturn(someIntegerBetween(1, 100));
            given(batchParameter.getBatchName()).willReturn(batchName);
            given(updateStockBatchProcessor.getSourceStocks(date)).willReturn(sourceStocks);

            willDoNothing().given(updateStockBatchProcessor)
                .updateStocks(eq(sourceStocks), any(IBatchProcessor.BatchProcessorParameters.class));

            actualMessage = batch.execute(batchParameter);
        }

        @Test
        void shouldCallUpdateStockBatchProcessor() {
            then(updateStockBatchProcessor).should()
                .updateStocks(eq(sourceStocks), any(IBatchProcessor.BatchProcessorParameters.class));
        }

        @Test
        void shouldReturnResultMessage() {
            assertThat(actualMessage, is(
                "[Batch] %s batch successfully finished. [creation: %s, modification: %s / %s] on %s"
                    .formatted(batchName, 0, 0, 0, date)
            ));
        }
    }
}
