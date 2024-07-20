package ag.act.validator;

import ag.act.entity.Solidarity;
import ag.act.exception.BadRequestException;
import ag.act.exception.InternalServerException;
import ag.act.model.Status;
import ag.act.validator.solidarity.SolidarityValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.stream.Stream;

import static ag.act.TestUtil.assertException;

@MockitoSettings(strictness = Strictness.LENIENT)
class SolidarityValidatorTest {
    @InjectMocks
    private SolidarityValidator validator;

    @Nested
    class WhenValidateUpdateSolidarityToActive {
        private Solidarity solidarity;

        @BeforeEach
        void setUp() {
            solidarity = new Solidarity();
        }


        @Nested
        class AndSolidarityStatusIsInactiveByAdmin {
            @Test
            void shouldReturnWithoutException() {

                // Given
                solidarity.setStatus(Status.INACTIVE_BY_ADMIN);

                // When / Then
                validator.validateUpdateSolidarityToActive(solidarity);
            }
        }

        @Nested
        class AndSolidarityStatusIsActive {
            @Test
            void shouldThrowBadRequestException() {

                // Given
                solidarity = new Solidarity();
                solidarity.setStatus(Status.ACTIVE);

                // When // Then
                assertException(
                    BadRequestException.class,
                    () -> validator.validateUpdateSolidarityToActive(solidarity),
                    "이미 활성화된 연대입니다."
                );
            }
        }

        @Nested
        class AndSolidarityStatusIsInvalid {
            @ParameterizedTest
            @MethodSource("invalidStatusProvider")
            void shouldThrowInternalServerException(Status status) {

                // Given
                solidarity.setStatus(status);

                // When // Then
                assertException(
                    InternalServerException.class,
                    () -> validator.validateUpdateSolidarityToActive(solidarity),
                    "연대 상태가 활성화 대기 상태가 아닙니다."
                );
            }

            private static Stream<Arguments> invalidStatusProvider() {
                return Stream.of(
                    Arguments.of(Status.DELETED_BY_USER),
                    Arguments.of(Status.DELETED_BY_ADMIN),
                    Arguments.of(Status.INACTIVE_BY_USER),
                    Arguments.of(Status.PROCESSING),
                    Arguments.of(Status.WITHDRAWAL_REQUESTED)
                );
            }
        }
    }
}
