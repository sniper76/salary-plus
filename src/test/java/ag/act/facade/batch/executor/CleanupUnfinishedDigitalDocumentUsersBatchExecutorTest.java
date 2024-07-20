package ag.act.facade.batch.executor;

import ag.act.dto.BatchParameter;
import ag.act.entity.digitaldocument.DigitalDocumentUser;
import ag.act.exception.ActRuntimeException;
import ag.act.facade.batch.BatchRunner;
import ag.act.service.digitaldocument.DigitalDocumentUserService;
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
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mockStatic;
import static shiver.me.timbers.data.random.RandomIntegers.someIntegerBetween;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class CleanupUnfinishedDigitalDocumentUsersBatchExecutorTest {
    @InjectMocks
    private CleanupUnfinishedDigitalDocumentUsersBatchExecutor batch;
    private List<MockedStatic<?>> statics;
    @Mock
    private DigitalDocumentUserService digitalDocumentUserService;
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
    class CleanupUnfinishedDigitalDocumentUsers {

        @Mock
        private DigitalDocumentUser digitalDocumentUser1;
        @Mock
        private DigitalDocumentUser digitalDocumentUser2;
        private int successCount;

        @BeforeEach
        void setUp() {
            successCount = 2;
            final List<DigitalDocumentUser> digitalDocumentUsers = List.of(digitalDocumentUser1, digitalDocumentUser2);

            given(DateTimeUtil.getCurrentFormattedDateTime()).willReturn(date);
            given(batchParameter.getBatchPeriod()).willReturn(someIntegerBetween(1, 100));
            given(batchParameter.getBatchName()).willReturn(batchName);
            given(digitalDocumentUserService.getUnfinishedDigitalDocumentUsersForCleanup()).willReturn(digitalDocumentUsers);

            willDoNothing().given(digitalDocumentUserService).deleteUserDigitalDocument(digitalDocumentUser1);

        }

        @Nested
        class WhenCleanDigitalDocumentsSuccess extends DefaultTestCases {

            @BeforeEach
            void setUp() {
                successCount = 2;

                willDoNothing().given(digitalDocumentUserService).deleteUserDigitalDocument(digitalDocumentUser2);

                actualMessage = batch.execute(batchParameter);
            }
        }

        @Nested
        class WhenFailedToDeleteOneOfDeleteDigitalDocumentUser extends DefaultTestCases {

            @BeforeEach
            void setUp() {
                successCount = 1;

                willThrow(new ActRuntimeException(someString(5)))
                    .given(digitalDocumentUserService).deleteUserDigitalDocument(digitalDocumentUser2);

                actualMessage = batch.execute(batchParameter);
            }
        }

        @SuppressWarnings({"unused", "JUnitMalformedDeclaration"})
        class DefaultTestCases {

            @Test
            void shouldReturnResultMessage() {
                assertThat(actualMessage, Matchers.is(
                    "[Batch] %s batch successfully finished. [deletion: %s / %s]"
                        .formatted(batchName, successCount, 2)
                ));
            }

            @Test
            void shouldCallGetDigitalDocumentUsersForCleanup() {
                then(digitalDocumentUserService).should().getUnfinishedDigitalDocumentUsersForCleanup();
            }

            @Test
            void shouldCallDeleteDigitalDocumentUser() {
                then(digitalDocumentUserService).should().deleteUserDigitalDocument(digitalDocumentUser1);
                then(digitalDocumentUserService).should().deleteUserDigitalDocument(digitalDocumentUser2);
            }
        }
    }
}
