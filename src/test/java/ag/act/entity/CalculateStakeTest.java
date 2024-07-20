package ag.act.entity;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class CalculateStakeTest {
    @ParameterizedTest()
    @MethodSource("calculateStakeProvider")
    void shouldCalculateSuccess(Long totalIssuedStockQuantity, Long totalSolidarityMemberStockQuantity, double expectedStake) {
        double actual = CalculateStake.builder()
            .totalIssuedStockQuantity(totalIssuedStockQuantity)
            .totalSolidarityMemberStockQuantity(totalSolidarityMemberStockQuantity)
            .build()
            .calculate();

        assertThat(actual, is(expectedStake));
    }

    private static Stream<Arguments> calculateStakeProvider() {
        return Stream.of(
            Arguments.of(100L, 50L, 50.0), // 50% stake
            Arguments.of(100L, 25L, 25.0), // 25% stake
            Arguments.of(10L, 5L, 50.0), // 50% stake with smaller values
            Arguments.of(10L, 1L, 10.0), // 10% stake with smaller values
            Arguments.of(1L, 1L, 100.0), // 100% stake with smallest positive values
            Arguments.of(3L, 2L, 66.66666666666667), // About 66.67% stake
            Arguments.of(5L, 2L, 40.0), // 40% stake
            Arguments.of(100L, 0L, 0.0), // 0% stake
            Arguments.of(0L, 0L, Double.NaN) // Edge case: 0 total issued stock
        );
    }
}