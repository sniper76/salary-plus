package ag.act.facade;

import ag.act.converter.StockRankingConverter;
import ag.act.entity.admin.StockRanking;
import ag.act.facade.stock.StockRankingFacade;
import ag.act.model.StockRankingDataResponse;
import ag.act.service.admin.stock.StockRankingService;
import ag.act.util.DecimalFormatUtil;
import ag.act.util.StockMarketValueUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mockStatic;

@MockitoSettings
class StockRankingFacadeTest {

    private static final int RANK_LIST_SIZE = 10;

    @InjectMocks
    private StockRankingFacade stockRankingFacade;
    @Mock
    private StockRankingService stockRankingService;
    @Mock
    private StockRankingConverter stockRankingConverter;
    @Mock
    List<StockRanking> stockRankings;
    @Mock
    private List<ag.act.model.StockRankingResponse> stockRankingResponses;

    private List<MockedStatic<?>> statics;

    @BeforeEach
    void setUp() {
        statics = List.of(
            mockStatic(DecimalFormatUtil.class),
            mockStatic(StockMarketValueUtil.class)
        );
    }

    @AfterEach
    void tearDown() {
        statics.forEach(MockedStatic::close);
    }

    @Nested
    class GetStockRankingsTopN {

        private PageRequest pageRequest;

        @BeforeEach
        void setUp() {
            pageRequest = PageRequest.of(0, RANK_LIST_SIZE, Sort.by("marketValueRank"));
            given(stockRankingService.findTopNStockRankings(pageRequest))
                .willReturn(stockRankings);
            given(stockRankingConverter.convert(stockRankings))
                .willReturn(stockRankingResponses);
        }

        @Test
        void shouldGetStockRankingTopNResponses() {
            StockRankingDataResponse response = stockRankingFacade.getTopNStockRankings(pageRequest);

            assertThat(response.getData(), is(stockRankingResponses));
            then(stockRankingService).should().findTopNStockRankings(pageRequest);
            then(stockRankingConverter).should().convert(stockRankings);
        }
    }
}