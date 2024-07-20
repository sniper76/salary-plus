package ag.act.facade.batch.executor;

import ag.act.dto.BatchParameter;
import ag.act.entity.Solidarity;
import ag.act.entity.SolidarityDailySummary;
import ag.act.facade.batch.BatchRunner;
import ag.act.service.solidarity.SolidarityDailySummaryService;
import ag.act.service.solidarity.SolidarityService;
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
import static org.mockito.Mockito.never;
import static shiver.me.timbers.data.random.RandomIntegers.someIntegerBetween;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class CreateSolidarityDailySummariesBatchExecutorTest {
    @InjectMocks
    private CreateSolidarityDailySummariesBatchExecutor batch;
    private List<MockedStatic<?>> statics;
    @Mock
    private SolidarityService solidarityService;
    @Mock
    private SolidarityDailySummaryService solidarityDailySummaryService;
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
    class CreateSolidarityDailySummaries {
        @Mock
        private Solidarity solidarity1;
        @Mock
        private Solidarity solidarity2;
        @Mock
        private Solidarity solidarity3;
        @Mock
        private SolidarityDailySummary existingSolidarityDailySummary1;
        @Mock
        private SolidarityDailySummary existingSolidarityDailySummary2;
        @Mock
        private SolidarityDailySummary solidarityDailySummary1;
        @Mock
        private SolidarityDailySummary solidarityDailySummary2;
        @Mock
        private SolidarityDailySummary solidarityDailySummary3;

        @BeforeEach
        void setUp() {
            given(DateTimeUtil.getCurrentFormattedDateTime())
                .willReturn(date);
            given(solidarityService.getAllSolidarities())
                .willReturn(List.of(solidarity1, solidarity2, solidarity3));
            given(batchParameter.getBatchPeriod())
                .willReturn(someIntegerBetween(1, 100));
            given(batchParameter.getBatchName()).willReturn(batchName);

            mockSolidarity(solidarity1, solidarityDailySummary1, existingSolidarityDailySummary1);
            mockSolidarity(solidarity2, solidarityDailySummary2, existingSolidarityDailySummary2);
            mockSolidarity(solidarity3, solidarityDailySummary3, null);

            willDoNothing().given(entityManager).persist(any(Solidarity.class));

            actualMessage = batch.execute(batchParameter);
        }

        private void mockSolidarity(Solidarity solidarity, SolidarityDailySummary summary, SolidarityDailySummary existingSummary) {
            given(solidarityDailySummaryService.createSolidarityDailySummary(solidarity))
                .willReturn(summary);
            given(solidarity.getMostRecentDailySummary())
                .willReturn(existingSummary);
        }

        @Test
        void shouldReturnResultMessage() {
            assertThat(actualMessage, Matchers.is(
                "[Batch] %s batch successfully finished. [creation: %s / %s]"
                    .formatted(batchName, 3, 3)
            ));
        }

        @Test
        void shouldSetSecondMostRecentDailySummaryId() {
            then(solidarity1).should().setSecondMostRecentDailySummary(existingSolidarityDailySummary1);
            then(solidarity2).should().setSecondMostRecentDailySummary(existingSolidarityDailySummary2);
            then(solidarity3).should(never()).setSecondMostRecentDailySummary(any(SolidarityDailySummary.class));
        }

        @Test
        void shouldSetMostRecentDailySummary() {
            then(solidarity1).should().setMostRecentDailySummary(solidarityDailySummary1);
            then(solidarity2).should().setMostRecentDailySummary(solidarityDailySummary2);
            then(solidarity3).should().setMostRecentDailySummary(solidarityDailySummary3);
        }

        @Test
        void shouldCallSaveSolidarity() {
            then(solidarityService).should().saveSolidarity(solidarity1);
            then(solidarityService).should().saveSolidarity(solidarity2);
            then(solidarityService).should().saveSolidarity(solidarity3);
        }
    }
}
