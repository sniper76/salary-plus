package ag.act.service.stock.home;

import ag.act.converter.home.CarouselItemConverter;
import ag.act.converter.home.StockHomeSectionResponseConverter;
import ag.act.model.StockHomeSectionResponse;
import ag.act.service.post.MostRecentThreePostQueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;

import static ag.act.TestUtil.someStockCode;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@MockitoSettings(strictness = Strictness.LENIENT)
class StockHomeCarouselSectionServiceTest {
    @InjectMocks
    private StockHomeCarouselSectionService service;
    @Mock
    private MostRecentThreePostQueryService mostRecentThreePostQueryService;
    @Mock
    private CarouselItemConverter carouselItemConverter;
    @Mock
    private StockHomeSectionResponseConverter stockHomeSectionResponseConverter;
    private String stockCode;

    @BeforeEach
    void setUp() {
        stockCode = someStockCode();
    }

    @Nested
    class WhenGenerateCarouselSection {
        @Nested
        class AndBoardsNotExists {
            @Test
            void shouldReturnNull() {
                // Given
                given(mostRecentThreePostQueryService.getPostsByStockCodeAndBoardGroup(anyString(), any(), any()))
                    .willThrow(RuntimeException.class);

                // When
                Optional<StockHomeSectionResponse> actual = Optional.ofNullable(service.getSections(stockCode));

                // Then
                assertThat(actual.isEmpty(), is(true));
                then(carouselItemConverter).shouldHaveNoInteractions();
                then(stockHomeSectionResponseConverter).shouldHaveNoInteractions();
            }

        }

    }


}