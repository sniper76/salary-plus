package ag.act.service.download.csv;

import ag.act.enums.BoardCategory;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static shiver.me.timbers.data.random.RandomThings.someThing;

@MockitoSettings(strictness = Strictness.LENIENT)
class DigitalDocumentCsvDownloadServiceTest {

    @InjectMocks
    private DigitalDocumentCsvDownloadService service;
    @Mock
    private DigitalDocumentCsvDownloadProcessor digitalDocumentCsvDownloadProcessor;

    @BeforeEach
    void setUp() {
        willDoNothing()
            .given(digitalDocumentCsvDownloadProcessor)
            .download(any(HttpServletResponse.class), anyList());

    }

    @Nested
    class IsSupport {

        private static Stream<Arguments> valueProvider() {
            return Stream.of(
                Arguments.of(BoardCategory.ETC, Boolean.TRUE),
                Arguments.of(BoardCategory.DIGITAL_DELEGATION, Boolean.TRUE),
                Arguments.of(BoardCategory.CO_HOLDING_ARRANGEMENTS, Boolean.TRUE),
                Arguments.of(BoardCategory.DAILY_ACT, Boolean.FALSE),
                Arguments.of(BoardCategory.FREE_DEBATE, Boolean.FALSE)
            );
        }

        @ParameterizedTest(name = "{index} => boardCategory=''{0}'', expectedResult=''{1}''")
        @MethodSource("valueProvider")
        void shouldReturnProperBooleanResult(BoardCategory boardCategory, Boolean expectedResult) {

            // When
            boolean actual = service.isSupport(new CsvDownloadSourceProvider<>(boardCategory, List.of()));

            // Then
            assertThat(actual, is(expectedResult));
        }
    }

    @Nested
    class Download {
        @Mock
        private HttpServletResponse response;

        @Test
        void shouldDownload() {

            // Given
            final List<Long> sourceIds = List.of();
            final CsvDownloadSourceProvider<List<Long>> sourceProvider = new CsvDownloadSourceProvider<>(
                someThing(
                    BoardCategory.ETC,
                    BoardCategory.DIGITAL_DELEGATION,
                    BoardCategory.CO_HOLDING_ARRANGEMENTS
                ),
                sourceIds
            );

            // When
            service.download(response, sourceProvider);

            // Then
            then(digitalDocumentCsvDownloadProcessor).should().download(response, sourceIds);
        }
    }
}
