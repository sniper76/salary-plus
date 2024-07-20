package ag.act.service;

import ag.act.dto.BatchParameter;
import ag.act.entity.BatchLog;
import ag.act.enums.BatchStatus;
import ag.act.exception.BadRequestException;
import ag.act.model.BatchRequest;
import ag.act.repository.BatchLogRepository;
import ag.act.util.BatchLogFinder;
import ag.act.util.DateTimeUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static ag.act.TestUtil.assertException;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static shiver.me.timbers.data.random.RandomEnums.someEnum;
import static shiver.me.timbers.data.random.RandomIntegers.someInteger;
import static shiver.me.timbers.data.random.RandomLongs.someLong;
import static shiver.me.timbers.data.random.RandomStrings.someString;
import static shiver.me.timbers.data.random.RandomThings.someThing;

@MockitoSettings(strictness = Strictness.LENIENT)
class BatchLogServiceTest {
    private BatchLogService service;
    @Mock
    private BatchLogRepository batchLogRepository;
    @Mock
    private BatchLogFinder batchLogFinder;
    @Mock
    private BatchParameter batchParameter;

    private List<MockedStatic<?>> statics;

    private Long batchLogId;
    private Integer batchPeriod;
    private BatchRequest.PeriodTimeUnitEnum periodTimeUnit;

    @BeforeEach
    void setUp() {
        statics = List.of(mockStatic(DateTimeUtil.class));

        batchLogId = someLong();
        batchPeriod = someInteger();
        periodTimeUnit = someEnum(BatchRequest.PeriodTimeUnitEnum.class);

        given(batchParameter.getBatchPeriod()).willReturn(batchPeriod);
        given(batchParameter.getPeriodTimeUnit()).willReturn(periodTimeUnit);

        service = new BatchLogService(batchLogRepository, batchLogFinder);
    }

    @AfterEach
    void tearDown() {
        statics.forEach(MockedStatic::close);
    }

    @Nested
    class LogIfCanProceed {
        private Optional<Long> result;
        @Captor
        private ArgumentCaptor<BatchLog> batchLogCaptor;

        @BeforeEach
        void setUp() {
            LocalDateTime targetStartTime = LocalDateTime.now();

            given(DateTimeUtil.getCurrentDateTimeMinusHours(batchPeriod)).willReturn(targetStartTime);
            given(batchLogFinder.findBatchLogByBatchNameWithInBatchPeriod(batchParameter)).willReturn(Optional.empty());
        }

        @Nested
        class AndNotFoundTheExistingBatchLogInDatabase {
            @Mock
            private BatchLog savedBatchLog;

            @Test
            void shouldSaveNewBatchLog() {

                // Given
                given(batchLogRepository.save(any(BatchLog.class))).willReturn(savedBatchLog);
                given(savedBatchLog.getId()).willReturn(someLong());

                // When
                result = service.logIfCanProceed(batchParameter);

                // Then
                assertThat(result.isPresent(), is(true));
                then(batchLogRepository).should().save(batchLogCaptor.capture());
                final BatchLog batchLog = batchLogCaptor.getValue();
                assertThat(batchLog.getBatchPeriod(), is(batchPeriod));
                assertThat(batchLog.getPeriodTimeUnit(), is(periodTimeUnit));
                assertThat(batchLog.getBatchStatus(), is(BatchStatus.IN_PROGRESS));
                assertThat(batchLog.getStartTime(), notNullValue());
                assertThat(batchLog.getEndTime(), nullValue());
                assertThat(batchLog.getResult(), nullValue());
            }
        }

        @Nested
        class AndAlreadyHaveTheExistingBatchLogInDatabase {

            @Mock
            private BatchLog existingBatchLog;


            @BeforeEach
            void setUp() {
                given(batchLogFinder.findBatchLogByBatchNameWithInBatchPeriod(batchParameter)).willReturn(Optional.of(existingBatchLog));
            }

            @Nested
            class AndBatchLogStatusIsFailure {

                @Mock
                private BatchLog savedBatchLog;


                @Test
                void shouldSaveNewBatchLog() {

                    // Given
                    given(batchLogRepository.save(any(BatchLog.class))).willReturn(savedBatchLog);
                    given(savedBatchLog.getId()).willReturn(someLong());
                    given(existingBatchLog.getBatchStatus()).willReturn(BatchStatus.FAILURE);

                    // When
                    result = service.logIfCanProceed(batchParameter);

                    // Then
                    assertThat(result.isPresent(), is(true));
                    then(batchLogRepository).should().save(any(BatchLog.class));
                }
            }

            @Nested
            class AndBatchLogStatusIsNotFailure {

                @Test
                void shouldNotSaveNewBatchLog() {

                    // Given
                    given(existingBatchLog.getBatchStatus())
                        .willReturn(someThing(BatchStatus.getNotFailureStatuses().toArray(new BatchStatus[0])));

                    // When
                    result = service.logIfCanProceed(batchParameter);

                    // Then
                    assertThat(result.isPresent(), is(false));
                    then(batchLogRepository).should(never()).save(any(BatchLog.class));
                }
            }
        }
    }

    @Nested
    class LogAfterReturning {

        @Mock
        private BatchLog existingBatchLog;

        @Nested
        class WhenExistingBatchLogIsFound {

            @Test
            void shouldUpdateBatchLog() {

                // Given
                final String result = someString(5);
                given(batchLogFinder.findLatestBatchLogByBatchLogId(batchLogId)).willReturn(Optional.of(existingBatchLog));

                // When
                service.logAfterReturning(batchLogId, result);

                // Then
                then(batchLogRepository).should().save(existingBatchLog);
                then(existingBatchLog).should().setResult(result);
                then(existingBatchLog).should().setEndTime(any(LocalDateTime.class));
                then(existingBatchLog).should().setBatchStatus(BatchStatus.SUCCESS);
            }
        }

        @Nested
        class WhenExistingBatchLogNotFound {

            @Test
            void shouldNotUpdateBatchLog() {

                // Given
                final String result = someString(5);
                given(batchLogFinder.findLatestBatchLogByBatchLogId(batchLogId)).willReturn(Optional.empty());

                // When
                assertException(
                    BadRequestException.class,
                    () -> service.logAfterReturning(batchLogId, result),
                    "Batch Log not found with batchLogId(%s)".formatted(batchLogId)
                );

                // Then
                then(batchLogRepository).should(never()).save(any(BatchLog.class));
            }
        }
    }

    @Nested
    class LogAfterThrowing {

        @Mock
        private BatchLog existingBatchLog;
        private String failureReason;

        @BeforeEach
        void setUp() {
            failureReason = someString(5);
        }

        @Nested
        class WhenExistingBatchLogIsFound {

            @Test
            void shouldUpdateBatchLog() {

                // Given
                given(batchLogFinder.findLatestBatchLogByBatchLogId(batchLogId)).willReturn(Optional.of(existingBatchLog));

                // When
                service.logAfterThrowing(batchLogId, failureReason);

                // Then
                then(batchLogRepository).should().save(existingBatchLog);
                then(existingBatchLog).should().setEndTime(any(LocalDateTime.class));
                then(existingBatchLog).should().setBatchStatus(BatchStatus.FAILURE);
                then(existingBatchLog).should().setResult(failureReason);
            }
        }

        @Nested
        class WhenExistingBatchLogNotFound {

            @Test
            void shouldNotUpdateBatchLog() {

                // Given
                given(batchLogFinder.findLatestBatchLogByBatchLogId(batchLogId)).willReturn(Optional.empty());

                // When
                assertException(
                    BadRequestException.class,
                    () -> service.logAfterThrowing(batchLogId, failureReason),
                    "Batch Log not found with batchLogId(%s)".formatted(batchLogId)
                );

                // Then
                then(batchLogRepository).should(never()).save(any(BatchLog.class));
            }
        }
    }
}
