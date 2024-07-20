package ag.act.validator;

import ag.act.exception.BadRequestException;
import ag.act.exception.InternalServerException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static ag.act.TestUtil.assertException;
import static ag.act.TestUtil.someInstantInTheFuture;
import static org.mockito.BDDMockito.given;

@MockitoSettings(strictness = Strictness.LENIENT)
class PollValidatorTest {

    private PollValidator validator;
    @Mock
    private ag.act.model.CreatePollRequest createPollRequest;

    @BeforeEach
    void setUp() {
        validator = new PollValidator();
    }

    @Nested
    class WhenEverythingIsFine {
        @Test
        void shouldNotThrowAnyException() {
            // Given
            final Instant instant = someInstantInTheFuture();
            given(createPollRequest.getTargetStartDate()).willReturn(instant);
            given(createPollRequest.getTargetEndDate()).willReturn(instant.plus(5, ChronoUnit.DAYS));

            // When / Then
            validator.validate(createPollRequest);
        }
    }

    @Nested
    class WhenCreatePollRequestIsNull {
        @Test
        void shouldThrowInternalServerException() {

            // When // then
            assertException(
                InternalServerException.class,
                () -> validator.validate(null),
                "설문을 생성하는 중에 알 수 없는 오류가 발생하였습니다. 잠시 후 다시 이용해 주세요."
            );
        }

    }

    @Nested
    class WhenTargetEndDateIsBeforeTargetStartDate {
        @Test
        void shouldThrowBadRequestException() {
            // Given
            final Instant instant = someInstantInTheFuture();
            given(createPollRequest.getTargetStartDate()).willReturn(instant);
            given(createPollRequest.getTargetEndDate()).willReturn(instant.minus(5, ChronoUnit.DAYS));

            // When // then
            assertException(
                BadRequestException.class,
                () -> validator.validate(createPollRequest),
                "설문 시작일이 종료일보다 크거나 같습니다."
            );
        }
    }
}
