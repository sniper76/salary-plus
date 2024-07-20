package ag.act.enums;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@MockitoSettings(strictness = Strictness.LENIENT)
class BoardCategoryTest {

    @Nested
    class IsDigitalDocumentActionGroup {

        @ParameterizedTest(name = "{index} => boardCategory=''{0}'', expectedValue=''{0}''")
        @MethodSource("valueProvider")
        void shouldCheckIfBoardCategoryIsDigitalDocumentActionGroup(BoardCategory boardCategory, boolean expectedValue) {
            final boolean actual = boardCategory.isDigitalDocumentActionGroup();

            assertThat(actual, is(expectedValue));
        }

        private static Stream<Arguments> valueProvider() {
            return Stream.of(
                Arguments.of(BoardCategory.DAILY_ACT, false),
                Arguments.of(BoardCategory.STOCK_CURRENT_ISSUES, false),
                Arguments.of(BoardCategory.TOPICS, false),
                Arguments.of(BoardCategory.WEEKLY_ISSUES, false),
                Arguments.of(BoardCategory.ANALYZE_REPORTS, false),
                Arguments.of(BoardCategory.SOLIDARITY_LEADER_LETTERS, false),
                Arguments.of(BoardCategory.DEBATE, false),
                Arguments.of(BoardCategory.SURVEYS, false),
                Arguments.of(BoardCategory.DIGITAL_DELEGATION, true),
                Arguments.of(BoardCategory.CO_HOLDING_ARRANGEMENTS, true),
                Arguments.of(BoardCategory.ETC, true)
            );
        }
    }
}