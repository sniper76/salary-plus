package ag.act.util;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@MockitoSettings(strictness = Strictness.LENIENT)
class DecimalFormatUtilTest {

    @Nested
    class FormatWithTwoDecimalPlaces {

        @ParameterizedTest(name = "{index} => doubleValue=''{0}'', expectedValue=''{1}''")
        @MethodSource("valueProvider")
        void shouldReturnFormattedString(String doubleValue, String expectedValue) {
            // Given
            final double value = Double.parseDouble(doubleValue);

            // When
            final String actualValue = DecimalFormatUtil.formatWithTwoDecimalPlaces(value);

            // Then
            assertThat(actualValue, is(expectedValue));
        }

        private static Stream<Arguments> valueProvider() {
            return Stream.of(
                Arguments.of("0.00", "0.00"),
                Arguments.of("0.005", "0.01"),
                Arguments.of("0.12", "0.12"),
                Arguments.of("1.129", "1.13"),
                Arguments.of("123.12111", "123.12"),
                Arguments.of("1234.155", "1234.16"),
                Arguments.of("12345.156", "12345.16"),
                Arguments.of("123456.157", "123456.16"),
                Arguments.of("500000.159", "500000.16"),
                Arguments.of("520000.159", "520000.16"),
                Arguments.of("525000.159", "525000.16")
            );
        }
    }
}