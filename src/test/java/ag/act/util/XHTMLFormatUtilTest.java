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
class XHTMLFormatUtilTest {

    @Nested
    class WhenConvertHtmlToXhtml {

        @ParameterizedTest(name = "{index} => html=''{0}'', expectedXhtml=''{1}''")
        @MethodSource("htmlProvider")
        void shouldConvertHtmlToXhtml(String htmlString, String expectedXhtmlString) {

            // When
            final String actual = XHTMLFormatUtil.convertHtmlToXhtml(htmlString);

            // Then
            assertThat(actual, is(expectedXhtmlString));
        }

        private static Stream<Arguments> htmlProvider() {
            return Stream.of(
                Arguments.of("<p>test<br></p>", "<html><head></head><body><p>test<br /></p></body></html>"),
                Arguments.of("<p>test<br><p>", "<html><head></head><body><p>test<br /></p><p></p></body></html>"),
                Arguments.of("<p>&nbsp;</p>", "<html><head></head><body><p>&#xa0;</p></body></html>")
            );
        }
    }
}
