package ag.act.facade.converter;

import ag.act.converter.home.CarouselHeaderImageConverter;
import ag.act.enums.BoardGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@MockitoSettings(strictness = Strictness.LENIENT)
class CarouselHeaderImageConverterTest {
    private static final String ANALYSIS_HEADER_IMAGE_URL = "analysisImageUrl";
    private static final String DEBATE_HEADER_IMAGE_URL = "debateImageUrl";
    private static final String DEFAULT_HEADER_IMAGE_URL = "defaultImageUrl";

    private CarouselHeaderImageConverter converter;

    @BeforeEach
    void setUp() {
        converter = new CarouselHeaderImageConverter(
            ANALYSIS_HEADER_IMAGE_URL,
            DEBATE_HEADER_IMAGE_URL,
            DEFAULT_HEADER_IMAGE_URL
        );
    }

    @Test
    void shouldReturnAnalysisHeaderImageUrl() {

        // When
        String actual = converter.convert(BoardGroup.ANALYSIS);

        // Then
        assertThat(actual, is(ANALYSIS_HEADER_IMAGE_URL));
    }

    @Test
    void shouldReturnDebateHeaderImageUrl() {

        // When
        String actual = converter.convert(BoardGroup.DEBATE);

        // Then
        assertThat(actual, is(DEBATE_HEADER_IMAGE_URL));
    }

    @Test
    void shouldReturnDefaultHeaderImageUrl() {

        // When
        String actual = converter.convert(BoardGroup.ACTION);

        // Then
        assertThat(actual, is(DEFAULT_HEADER_IMAGE_URL));
    }
}
