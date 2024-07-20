package ag.act.util.badge;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;

@MockitoSettings(strictness = Strictness.LENIENT)
class BadgeLabelGeneratorTest {

    @InjectMocks
    private BadgeLabelGenerator generator;

    @Nested
    class GenerateStockQuantityBadge {

        private static Stream<Arguments> quantityAndBadgeInputProvider() {
            return Stream.of(
                Arguments.of(0L, null),
                Arguments.of(1L, "1주+"),
                Arguments.of(10L, "1주+"),
                Arguments.of(99L, "1주+"),
                Arguments.of(100L, "100주+"),
                Arguments.of(1_000L, "1000주+"),
                Arguments.of(3_000L, "1000주+"),
                Arguments.of(5_000L, "5000주+"),
                Arguments.of(10_000L, "1만주+"),
                Arguments.of(30_000L, "1만주+"),
                Arguments.of(50_000L, "5만주+"),
                Arguments.of(70_000L, "5만주+"),
                Arguments.of(100_000L, "10만주+"),
                Arguments.of(120_000L, "10만주+"),
                Arguments.of(300_000L, "10만주+"),
                Arguments.of(500_000L, "50만주+"),
                Arguments.of(1_000_000L, "100만주+"),
                Arguments.of(1_200_000L, "100만주+"),
                Arguments.of(5_000_000L, "500만주+"),
                Arguments.of(5_000_001L, "500만주+"),
                Arguments.of(10_000_000L, "1000만주+"),
                Arguments.of(10_000_001L, "1000만주+"),
                Arguments.of(50_000_000L, "5000만주+"),
                Arguments.of(100_000_000L, "1억주+"),
                Arguments.of(Long.MAX_VALUE, "1억주+")
            );
        }

        @ParameterizedTest(name = "{index} => quantity=''{0}'', expectedBadge=''{1}''")
        @MethodSource("quantityAndBadgeInputProvider")
        void shouldReturnProperBadge(Long quantity, String expectedBadge) {

            // When
            final String actual = generator.generateStockQuantityBadge(quantity);

            // Then
            assertThat(actual, Matchers.is(expectedBadge));
        }
    }

    @Nested
    class GenerateAssetBadge {

        private static Stream<Arguments> quantityAndBadgeInputProvider() {
            return Stream.of(
                Arguments.of(1L, null),
                Arguments.of(10L, null),
                Arguments.of(99L, null),
                Arguments.of(1_000_000L, null),
                Arguments.of(10_000_000L, null),
                Arguments.of(100_000_000L, "1억+"),
                Arguments.of(1_000_000_000L, "10억+"),
                Arguments.of(10_000_000_000L, "100억+"),
                Arguments.of(100_000_000_000L, "100억+")
            );
        }

        @ParameterizedTest(name = "{index} => asset=''{0}'', expectedBadge=''{1}''")
        @MethodSource("quantityAndBadgeInputProvider")
        void shouldReturnProperBadge(Long asset, String expectedBadge) {

            // When
            final String actual = generator.generateAssetBadge(asset);

            // Then
            assertThat(actual, Matchers.is(expectedBadge));
        }
    }
}
