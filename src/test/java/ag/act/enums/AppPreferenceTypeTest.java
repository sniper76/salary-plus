package ag.act.enums;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class AppPreferenceTypeTest {

    @Nested
    class WhenValidate {

        @Nested
        class WhenNewThresholdHours {

            private static final AppPreferenceType appPreferenceType = AppPreferenceType.NEW_POST_THRESHOLD_HOURS;

            private static Stream<Arguments> validValueProvider() {
                return Stream.of(
                    Arguments.of("2"),
                    Arguments.of("23"),
                    Arguments.of("192"),
                    Arguments.of("17287394"),
                    Arguments.of("30000000000"),
                    Arguments.of("5000000000000000"),
                    Arguments.of("3726054837262840583376")
                );
            }

            private static Stream<Arguments> invalidValueProvider() {
                return Stream.of(
                    Arguments.of("12384Abdof"),
                    Arguments.of("12kdia295"),
                    Arguments.of("dkfi2948"),
                    Arguments.of("-1827"),
                    Arguments.of("384.284"),
                    Arguments.of("3847,"),
                    Arguments.of("3583,19358"),
                    Arguments.of("29."),
                    Arguments.of(".34")
                );
            }

            @ParameterizedTest(name = "{index} => newValue=''{0}''")
            @MethodSource("validValueProvider")
            void shouldReturnTrue(String newValue) {
                assertThat(appPreferenceType.isValueMatches(newValue), is(true));
            }

            @ParameterizedTest(name = "{index} => newValue=''{0}''")
            @MethodSource("invalidValueProvider")
            void shouldReturnFalse(String newValue) {
                assertThat(appPreferenceType.isValueMatches(newValue), is(false));
            }
        }

        @Nested
        class WhenMinAppVersion {

            private static final AppPreferenceType appPreferenceType = AppPreferenceType.MIN_APP_VERSION;

            private static Stream<Arguments> validValueProvider() {
                return Stream.of(
                    Arguments.of("2.1.1"),
                    Arguments.of("1.1.1"),
                    Arguments.of("23.545.64"),
                    Arguments.of("1.35.657345"),
                    Arguments.of("1243.35344.3645"),
                    Arguments.of("342354.245345354.23525453"),
                    Arguments.of("0.0.0")
                );
            }

            private static Stream<Arguments> invalidValueProvider() {
                return Stream.of(
                    Arguments.of("23423.4234"),
                    Arguments.of("23423"),
                    Arguments.of("23423."),
                    Arguments.of("-234.23423.325"),
                    Arguments.of("384.284.23423."),
                    Arguments.of("234,2342,23"),
                    Arguments.of("2,2,2,"),
                    Arguments.of("3,3,3"),
                    Arguments.of(",234,234"),
                    Arguments.of(".3.3.3"),
                    Arguments.of(".2.2"),
                    Arguments.of("eif39483,39472.3972"),
                    Arguments.of("djf.384.djf")
                );
            }

            @ParameterizedTest(name = "{index} => newValue=''{0}''")
            @MethodSource("validValueProvider")
            void shouldReturnTrue(String newValue) {
                assertThat(appPreferenceType.isValueMatches(newValue), is(true));
            }

            @ParameterizedTest(name = "{index} => newValue=''{0}''")
            @MethodSource("invalidValueProvider")
            void shouldReturnFalse(String newValue) {
                assertThat(appPreferenceType.isValueMatches(newValue), is(false));
            }
        }


        @Nested
        class WhenBlockExceptUserIds {

            private static final AppPreferenceType appPreferenceType = AppPreferenceType.BLOCK_EXCEPT_USER_IDS;

            private static Stream<Arguments> validValueProvider() {
                return Stream.of(
                    Arguments.of("2,3"),
                    Arguments.of("23"),
                    Arguments.of("192,18274,98493,493793,30739"),
                    Arguments.of("1,2,2,3,4,3,5,4,6,6,56,7,8"),
                    Arguments.of("2483739279,397293739739,30587394739375,393764836384,357394739373,303874684638"),
                    Arguments.of("93839,304829482,39473947384"),
                    Arguments.of("39,349,239,3,3857")
                );
            }

            private static Stream<Arguments> invalidValueProvider() {
                return Stream.of(
                    Arguments.of("12384Abdof"),
                    Arguments.of("123,231,"),
                    Arguments.of(",1,23,4,56,"),
                    Arguments.of(",1,23,4,56"),
                    Arguments.of("12kdia295"),
                    Arguments.of("dkfi2948"),
                    Arguments.of("-1827"),
                    Arguments.of("384.284"),
                    Arguments.of("3847,"),
                    Arguments.of("3583,19358.e39489"),
                    Arguments.of("29."),
                    Arguments.of(".34")
                );
            }

            @ParameterizedTest(name = "{index} => newValue=''{0}''")
            @MethodSource("validValueProvider")
            void shouldReturnTrue(String newValue) {
                assertThat(appPreferenceType.isValueMatches(newValue), is(true));
            }

            @ParameterizedTest(name = "{index} => newValue=''{0}''")
            @MethodSource("invalidValueProvider")
            void shouldReturnFalse(String newValue) {
                assertThat(appPreferenceType.isValueMatches(newValue), is(false));
            }
        }
    }
}