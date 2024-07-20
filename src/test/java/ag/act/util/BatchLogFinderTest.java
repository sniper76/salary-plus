package ag.act.util;

import ag.act.dto.BatchParameter;
import ag.act.entity.BatchLog;
import ag.act.repository.BatchLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@SuppressWarnings("checkstyle:LineLength")
@MockitoSettings(strictness = Strictness.LENIENT)
class BatchLogFinderTest {

    @InjectMocks
    private BatchLogFinder finder;

    @Mock
    private BatchLogRepository batchLogRepository;
    @Mock
    private BatchParameter batchParameter;
    private String batchName;

    @BeforeEach
    void setUp() {
        batchName = someString(5);

        given(batchParameter.getBatchName()).willReturn(batchName);
    }

    @Nested
    class WhenExistingBatchLogFound {

        @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
        @Mock
        private Optional<BatchLog> existingBatchLog;

        @Test
        void shouldReturnBatchLog() {

            // Given
            given(batchLogRepository.findFirstByBatchNameAndStartTimeGreaterThanEqualOrderByUpdatedAtDesc(eq(batchName), any(LocalDateTime.class)))
                .willReturn(existingBatchLog);

            // When
            final Optional<BatchLog> actual = finder.findBatchLogByBatchNameWithInBatchPeriod(batchParameter);

            // Then
            assertThat(actual, is(existingBatchLog));
        }
    }
}
