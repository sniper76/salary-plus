package ag.act.enums;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static shiver.me.timbers.data.random.RandomIntegers.someIntegerBetween;

@MockitoSettings(strictness = Strictness.LENIENT)
class NationalityTest {

    @Nested
    class GetTrimmedName {
        @Nested
        class WhenKorean {
            @ParameterizedTest(name = "{index} => name=''{0}'', expected=''{1}''")
            @MethodSource("valueProvider")
            void shouldReturnTrimmedName(String name, String expected) {

                // When
                final String actual = Nationality.KOREAN.getTrimmedName(name);

                // Then
                assertThat(actual, is(expected));
            }

            private static Stream<Arguments> valueProvider() {
                return Stream.of(
                    Arguments.of(" 이   동   훈    ", "이동훈"),
                    Arguments.of(" 이   동훈    ", "이동훈"),
                    Arguments.of(" 이동   훈    ", "이동훈"),
                    Arguments.of("     이동훈    ", "이동훈"),
                    Arguments.of("이동훈", "이동훈")
                );
            }
        }

        @Nested
        class WhenForeigner {
            @ParameterizedTest(name = "{index} => name=''{0}'', expected=''{0}''")
            @MethodSource("valueProvider")
            void shouldReturnTrimmedName(String name, String expected) {

                // When
                final String actual = Nationality.FOREIGNER.getTrimmedName(name);

                // Then
                assertThat(actual, is(expected));
            }

            private static Stream<Arguments> valueProvider() {
                return Stream.of(
                    Arguments.of(" Lucas Lee    ", "Lucas Lee"),
                    Arguments.of(" Lucas   Lee    ", "Lucas Lee"),
                    Arguments.of("        Lucas   Lee    ", "Lucas Lee"),
                    Arguments.of("Lucas    Lee    ", "Lucas Lee"),
                    Arguments.of("    LucasLee    ", "LucasLee"),
                    Arguments.of("BAEK SUNG JUN", "BAEK SUNG JUN", someIntegerBetween(5, 8)),
                    Arguments.of("Lucas Lee", "Lucas Lee")
                );
            }
        }
    }
}
