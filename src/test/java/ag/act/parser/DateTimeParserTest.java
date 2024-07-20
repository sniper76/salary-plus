package ag.act.parser;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@MockitoSettings(strictness = Strictness.LENIENT)
class DateTimeParserTest {


    @Test
    void shouldParseYyyyMmDd() {

        // Given

        // When
        final LocalDateTime localDateTime = DateTimeParser.parseDate("19780308");

        // Then
        assertThat(localDateTime, is(Matchers.notNullValue()));
    }
}
