package ag.act.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.stream.Stream;

import static ag.act.TestUtil.someStrongPassword;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static shiver.me.timbers.data.random.RandomIntegers.someIntegerBetween;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;
import static shiver.me.timbers.data.random.RandomStrings.someNumericString;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class PasswordUtilTest {
    private PasswordUtil util;
    private Integer minLength;
    private Integer maxLength;

    @BeforeEach
    void setUp() {
        minLength = someIntegerBetween(8, 10);
        maxLength = someIntegerBetween(11, 20);

        util = new PasswordUtil(
            minLength,
            maxLength
        );
    }

    @Nested
    class IsStrongPassword {
        @Nested
        class ValidateSuccess {

            @RepeatedTest(100)
            void shouldReturnTrue() {

                // Given
                final String password = someStrongPassword(minLength, maxLength);

                // When
                final boolean actualValue = util.isStrongPassword(password);

                // Then
                assertThat(actualValue, is(true));
            }
        }

        @Nested
        class FailToValidate {

            @ParameterizedTest(name = "{index} => password=''{0}''")
            @MethodSource("valueProvider")
            void shouldReturnFalse(String password) {

                // When
                final boolean actualValue = util.isStrongPassword(password);

                // Then
                assertThat(actualValue, is(false));
            }

            private static Stream<Arguments> valueProvider() {
                return Stream.of(
                    Arguments.of((String) null),
                    Arguments.of(""),
                    Arguments.of("   "),
                    Arguments.of(someString(7)),
                    Arguments.of(someAlphanumericString(8)),
                    Arguments.of(someNumericString(8)),
                    Arguments.of(someString(40)),
                    Arguments.of(someString(60)),
                    Arguments.of(someString(80)),
                    Arguments.of(someString(100))
                );
            }
        }
    }
}