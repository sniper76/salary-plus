package ag.act.facade.batch.executor;

import ag.act.dto.BatchParameter;
import ag.act.entity.User;
import ag.act.facade.batch.BatchRunner;
import ag.act.facade.user.UserFacade;
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

import java.time.LocalDateTime;
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
class UpdateWithdrawalRequestUsersBatchExecutorTest {

    @InjectMocks
    private UpdateWithdrawalRequestUsersBatchExecutor batch;
    private List<MockedStatic<?>> statics;
    @Mock
    private UserFacade userFacade;
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
    class UpdateWithdrawalRequestUsers {

        @Mock
        private User user1;
        @Mock
        private User user2;

        @BeforeEach
        void setUp() {
            final LocalDateTime updatedAtForWithdrawal = LocalDateTime.now().minusDays(1).minusSeconds(1);

            given(DateTimeUtil.getCurrentFormattedDateTime()).willReturn(date);
            given(batchParameter.getBatchPeriod()).willReturn(someIntegerBetween(1, 100));
            given(batchParameter.getBatchName()).willReturn(batchName);

            given(userFacade.getWithdrawRequestedUsersBeforeOneDay())
                .willReturn(List.of(user1, user2));
            given(DateTimeUtil.isBeforeInDays(updatedAtForWithdrawal, 1)).willReturn(true);

            given(user1.getUpdatedAt()).willReturn(updatedAtForWithdrawal);
            given(user2.getUpdatedAt()).willReturn(updatedAtForWithdrawal);

            given(userFacade.withdrawUser(user1, ag.act.model.Status.DELETED_BY_USER)).willReturn(Boolean.TRUE);
            given(userFacade.withdrawUser(user2, ag.act.model.Status.DELETED_BY_USER)).willReturn(Boolean.TRUE);

            actualMessage = batch.execute(batchParameter);
        }

        @Test
        void shouldReturnResultMessage() {
            assertThat(actualMessage, Matchers.is(
                "[Batch] %s batch successfully finished. [deletion: %s / %s]"
                    .formatted(batchName, 2, 2)
            ));
        }

        @Test
        void shouldCallWithdrawUserOfUserFacade() {
            then(userFacade).should().withdrawUser(user1, ag.act.model.Status.DELETED_BY_USER);
            then(userFacade).should().withdrawUser(user2, ag.act.model.Status.DELETED_BY_USER);
        }
    }
}
