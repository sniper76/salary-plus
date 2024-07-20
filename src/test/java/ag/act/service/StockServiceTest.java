package ag.act.service;

import ag.act.entity.Stock;
import ag.act.repository.StockRepository;
import ag.act.service.stock.StockService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class StockServiceTest {
    @InjectMocks
    private StockService service;
    @Mock
    private StockRepository stockRepository;

    @Nested
    class WhenGetMostRelatedTopTenStocksBySearchKeyword {
        private String searchKeyword;
        @Mock
        private List<Stock> stockList;

        @Nested
        class AndSearchKeywordIsNull {

            @Test
            void shouldGetMostRelatedTopTenStocksBySearchKeyword() {

                // Given
                searchKeyword = null;
                given(stockRepository.findTop10ByStatusIn(List.of(ag.act.model.Status.ACTIVE)))
                    .willReturn(stockList);

                // When
                service.getMostRelatedTopTenStocksBySearchKeyword(searchKeyword);

                // Then
                then(stockRepository).should().findTop10ByStatusIn(List.of(ag.act.model.Status.ACTIVE));
            }
        }

        @Nested
        class AndSearchKeywordIsEmpty {
            @Test
            void shouldGetMostRelatedTopTenStocksBySearchKeyword() {

                // Given
                searchKeyword = "";
                given(stockRepository.findTop10ByStatusIn(List.of(ag.act.model.Status.ACTIVE)))
                    .willReturn(stockList);

                // When
                service.getMostRelatedTopTenStocksBySearchKeyword(searchKeyword);

                // Then
                then(stockRepository).should().findTop10ByStatusIn(List.of(ag.act.model.Status.ACTIVE));

            }
        }

        @Nested
        class AndSearchKeywordIsPresent {
            @Test
            void shouldGetMostRelatedTopTenStocksBySearchKeyword() {

                // Given
                searchKeyword = someString(1, 100);
                given(stockRepository.findTop10ByNameContainingAndStatusIn(searchKeyword, List.of(ag.act.model.Status.ACTIVE)))
                    .willReturn(stockList);

                // When
                service.getMostRelatedTopTenStocksBySearchKeyword(searchKeyword);

                // Then
                then(stockRepository).should().findTop10ByNameContainingAndStatusIn(
                    searchKeyword, List.of(ag.act.model.Status.ACTIVE)
                );
            }
        }
    }
}