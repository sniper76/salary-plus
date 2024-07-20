package ag.act.service.stock.home;

import ag.act.model.StockHomeSectionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;

import static ag.act.TestUtil.someStockCode;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@MockitoSettings(strictness = Strictness.LENIENT)
class StockHomeSectionServiceTest {
    @InjectMocks
    private StockHomeSectionService service;
    @Mock
    private StockHomeCarouselSectionService stockHomeCarouselSectionService;
    @Mock
    private StockHomeListSectionService stockHomeListSectionService;
    @Mock
    private ag.act.model.StockHomeSectionResponse stockHomeSectionResponse;
    private String stockCode;

    @BeforeEach
    void setUp() {
        stockCode = someStockCode();
    }

    @Nested
    class WhenGenerateSections {
        @Test
        void shouldGenerateSections() {
            // When
            given(stockHomeCarouselSectionService.getSections(stockCode))
                .willReturn(stockHomeSectionResponse);
            given(stockHomeListSectionService.getSections(stockCode))
                .willReturn(List.of(stockHomeSectionResponse));

            // When
            List<StockHomeSectionResponse> actual = service.getSections(stockCode);

            // Then
            assertThat(actual.size(), is(2));
            then(stockHomeCarouselSectionService).should().getSections(stockCode);
            then(stockHomeListSectionService).should().getSections(stockCode);
        }
    }
}
