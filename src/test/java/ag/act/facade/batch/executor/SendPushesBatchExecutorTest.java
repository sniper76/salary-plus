package ag.act.facade.batch.executor;

import ag.act.dto.BatchParameter;
import ag.act.entity.Push;
import ag.act.facade.PushFacade;
import ag.act.facade.batch.BatchRunner;
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
class SendPushesBatchExecutorTest {
    @InjectMocks
    private SendPushesBatchExecutor batch;
    private List<MockedStatic<?>> statics;
    @Mock
    private PushFacade pushFacade;
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
    class SendPushes {

        @Mock
        private Push push1;
        @Mock
        private Push push2;

        @BeforeEach
        void setUp() {
            given(DateTimeUtil.getCurrentFormattedDateTime()).willReturn(date);
            given(batchParameter.getBatchPeriod()).willReturn(someIntegerBetween(1, 100));
            given(batchParameter.getBatchName()).willReturn(batchName);
            given(pushFacade.getPushListToSend()).willReturn(List.of(push1, push2));
            willDoNothing().given(pushFacade).sendPush(any(Push.class));

            actualMessage = batch.execute(batchParameter);
        }

        @Test
        void shouldReturnResultMessage() {
            assertThat(actualMessage, Matchers.is(
                "[Batch] %s batch successfully finished. [sent: %s / %s]"
                    .formatted(batchName, 2, 2)
            ));
        }

        @Test
        void shouldCallGetPushListToSend() {
            then(pushFacade).should().getPushListToSend();
        }

        @Test
        void shouldCallSendPush() {
            then(pushFacade).should().sendPush(push1);
            then(pushFacade).should().sendPush(push2);
        }
    }
}
