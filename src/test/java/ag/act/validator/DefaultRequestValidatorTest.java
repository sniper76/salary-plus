package ag.act.validator;

import ag.act.exception.BadRequestException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static shiver.me.timbers.data.random.RandomLongs.someLong;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class DefaultRequestValidatorTest {

    @InjectMocks
    private DefaultRequestValidator validator;

    @Nested
    class ValidateNotNullWithStringValue {

        @Nested
        class WhenValueIsNotNull {
            @Test
            void shouldNotThrowAnyException() {

                // Given
                final String value = someAlphanumericString(20);
                final String message = someAlphanumericString(30);

                // When
                validator.validateNotNull(value, message);
            }
        }

        @Nested
        class WhenValueIsInvalid {

            @ParameterizedTest(name = "{index} => inputDate=''{0}''")
            @MethodSource("invalidValueProvider")
            void shouldThrowBadRequestException(String value) {

                // Given
                final String message = someString(5);

                // When
                final BadRequestException exception = assertThrows(
                    BadRequestException.class,
                    () -> validator.validateNotNull(value, message)
                );

                // Then
                assertThat(exception.getMessage(), is(message));
            }

            private static Stream<Arguments> invalidValueProvider() {
                return Stream.of(
                    Arguments.of((String) null),
                    Arguments.of(""),
                    Arguments.of(" "),
                    Arguments.of("             ")
                );
            }
        }
    }

    @Nested
    class ValidateNotNullWithLongValue {

        @Nested
        class WhenValueIsNotNull {
            @Test
            void shouldNotThrowAnyException() {

                // Given
                final Long value = someLong();
                final String message = someString(5);

                // When
                validator.validateNotNull(value, message);
            }
        }

        @Nested
        class WhenValueIsInvalid {

            @Test
            void shouldThrowBadRequestException() {

                // Given
                final String message = someString(5);

                // When
                final BadRequestException exception = assertThrows(
                    BadRequestException.class,
                    () -> validator.validateNotNull((Long) null, message)
                );

                // Then
                assertThat(exception.getMessage(), is(message));
            }
        }
    }
}
