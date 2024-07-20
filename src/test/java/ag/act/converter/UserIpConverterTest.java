package ag.act.converter;

import ag.act.converter.user.UserIpConverter;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ag.act.TestUtil.someIpAddress;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;

@MockitoSettings(strictness = Strictness.LENIENT)
class UserIpConverterTest {

    @InjectMocks
    private UserIpConverter converter;

    @Nested
    class ConvertUserIp {
        @Nested
        class WhenUserIpIsInvalid {
            @ParameterizedTest(name = "{index} => userIp=''{0}''")
            @MethodSource("invalidUserIpProvider")
            void shouldReturnDefaultIpPrefix(String userIp) {
                // when
                final String actual = converter.convert(userIp);

                // then
                assertThat(actual, is("192.168"));
            }

            private static Stream<Arguments> invalidUserIpProvider() {
                return Stream.of(
                    Arguments.of(" "),
                    Arguments.of(""),
                    Arguments.of("    "),
                    Arguments.of((String) null),
                    Arguments.of(someAlphanumericString(20)),
                    Arguments.of("127.0.0.1.100"),
                    Arguments.of("500.0.0.1.1.200.300")
                );
            }
        }

        @Nested
        class WhenUserIpIsValid {
            @Test
            void shouldReturnFirstTwoDigitsOfIp() {

                // Given
                final String fullIpAddress = someIpAddress();
                final String expectedFirstTwoOctets = getFirstTwoOctets(fullIpAddress);

                // When
                final String actual = converter.convert(fullIpAddress);

                // Then
                assertThat(actual, is(expectedFirstTwoOctets));
            }

            private String getFirstTwoOctets(String fullIpAddress) {
                return Arrays.stream(fullIpAddress.split("\\."))
                    .limit(2)
                    .collect(Collectors.joining("."));
            }
        }

    }
}