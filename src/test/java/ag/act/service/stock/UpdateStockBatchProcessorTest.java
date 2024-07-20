package ag.act.service.stock;

import ag.act.entity.Board;
import ag.act.entity.Solidarity;
import ag.act.entity.Stock;
import ag.act.entity.StockReferenceDate;
import ag.act.enums.BoardCategory;
import ag.act.module.krx.KrxStockAggregator;
import ag.act.module.krx.KrxStockMapper;
import ag.act.service.BoardService;
import ag.act.service.IBatchProcessor.BatchProcessorParameters;
import ag.act.service.solidarity.SolidarityService;
import ag.act.util.ChunkUtil;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static shiver.me.timbers.data.random.RandomIntegers.someIntegerBetween;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class UpdateStockBatchProcessorTest {

    @InjectMocks
    private UpdateStockBatchProcessor processor;
    @Mock
    private TestStockService testStockService;
    @Mock
    private PrivateStockService privateStockService;
    @Mock
    private KrxStockAggregator krxStockAggregator;
    @Mock
    private StockService stockService;
    @Mock
    private SolidarityService solidarityService;
    @Mock
    private RegularGeneralMeetingStockReferenceDateService regularGeneralMeetingStockReferenceDateService;
    @Mock
    private BoardService boardService;
    @Mock
    private ChunkUtil chunkUtil;
    @Mock
    private KrxStockMapper krxStockMapper;
    @Mock
    private EntityManager entityManager;
    @Mock
    private StockChangeChecker stockChangeChecker;

    @Nested
    class GetSourceStocks {
        @Mock
        private Stock stock1;
        @Mock
        private Stock stock2;
        @Mock
        private Stock stock3;
        private String date;

        @BeforeEach
        void setUp() {
            date = someString(5);

            given(krxStockAggregator.getStocksFromKrxService(date)).willReturn(List.of(stock1));
            given(testStockService.getStocks()).willReturn(List.of(stock2));
            given(privateStockService.getActiveStocks()).willReturn(List.of(stock3));
        }

        @Test
        void shouldReturnStocks() {
            final List<Stock> stocks = processor.getSourceStocks(date);

            assertThat(stocks, is(List.of(stock1, stock2, stock3)));
        }
    }

    @Nested
    class UpdateStocks {
        @Mock
        private Stock krxStock1ForInsert;
        @Mock
        private Stock krxStock2ForInsert;
        @Mock
        private Stock krxStock3ForUpdate;
        @Mock
        private Stock krxStock3;
        @Mock
        private Stock testStock1ForInsert;
        @Mock
        private Stock testStock2ForUpdate;
        @Mock
        private Stock testStock2;
        @Mock
        private Solidarity solidarity;
        @Mock
        private StockReferenceDate stockReferenceDate;
        @Mock
        private Consumer<Integer> batchCountLog;
        @Mock
        private AtomicInteger updateCount;
        @Mock
        private AtomicInteger createCount;
        private int newStockCount;
        private List<Stock> sourceStocks;

        @BeforeEach
        void setUp() {
            int chunkSize = someIntegerBetween(10, 100);
            BatchProcessorParameters batchProcessorParameters = new BatchProcessorParameters(
                batchCountLog, updateCount, createCount, chunkSize
            );
            newStockCount = 3;

            sourceStocks = List.of(
                krxStock1ForInsert,
                krxStock2ForInsert,
                krxStock3ForUpdate,
                testStock1ForInsert,
                testStock2ForUpdate
            );

            willDoNothing().given(entityManager).persist(any(Solidarity.class));
            given(regularGeneralMeetingStockReferenceDateService.createIfNotFound(any(Stock.class)))
                .willReturn(stockReferenceDate);

            final String code1 = someString(7);
            final String code2 = someString(9);
            final String code3 = someString(11);
            final String code4 = someString(9);
            final String code5 = someString(11);
            final List<String> stockCodes = List.of(code1, code2, code3, code4, code5);

            final List<Optional<Stock>> stocks = List.of(
                mockStock(krxStock1ForInsert, null, code1, false),
                mockStock(krxStock2ForInsert, null, code2, false),
                mockStock(krxStock3ForUpdate, krxStock3, code3, true),
                mockStock(testStock1ForInsert, null, code4, false),
                mockStock(testStock2ForUpdate, testStock2, code5, true)
            );

            given(chunkUtil.getChunks(anyList(), anyInt()))
                .willAnswer(invocationOnMock -> List.of(invocationOnMock.getArguments()[0]));
            given(stockService.findAllByCodes(stockCodes))
                .willReturn(stocks.stream().filter(Optional::isPresent).map(Optional::get).toList());

            given(krxStockMapper.mergeStocks(any(), any())).willAnswer(invocation -> invocation.getArgument(1));
            doNothing().when(stockChangeChecker)
                .checkStockTotalIssuedQuantity(any(Stock.class), any(Stock.class));

            processor.updateStocks(sourceStocks, batchProcessorParameters);
        }

        private Optional<Stock> mockStock(Stock stockFromKrx, Stock stock, String code, boolean isExist) {
            given(stockFromKrx.getCode()).willReturn(code);

            if (isExist) {
                final List<Board> boardList = Arrays.stream(BoardCategory.activeBoardCategoriesForStocks())
                    .map(boardCategory -> {
                        final Board mockBoard = mock(Board.class);
                        given(mockBoard.getCategory()).willReturn(boardCategory);
                        return mockBoard;
                    })
                    .toList();

                given(stock.getCode()).willReturn(code);
                given(solidarityService.findSolidarity(code)).willReturn(Optional.of(solidarity));
                given(boardService.getBoards(code))
                    .willReturn(
                        boardList
                    );
                willDoNothing().given(entityManager).persist(stock);
            } else {
                given(stockService.findByCode(code)).willReturn(Optional.empty());
                given(boardService.getBoards(code)).willReturn(List.of());
                willDoNothing().given(entityManager).persist(stockFromKrx);
            }

            return Optional.ofNullable(stock);
        }

        @Test
        void shouldCallEntityManager() {
            then(entityManager).should().persist(krxStock1ForInsert);
            then(entityManager).should().persist(krxStock2ForInsert);
            then(entityManager).should().persist(krxStock3);
            then(entityManager).should().persist(testStock1ForInsert);
            then(entityManager).should().persist(testStock2);
        }

        @Test
        void shouldCreateNewStocks() {
            then(entityManager).should(times(newStockCount)).persist(any(Solidarity.class));
        }

        @Test
        void shouldCreateNewRegularGeneralMeetingStockReferenceDates() {
            then(regularGeneralMeetingStockReferenceDateService).should(times(sourceStocks.size())).createIfNotFound(any(Stock.class));
        }

        @Test
        void shouldCreateNewBoards() {
            then(entityManager).should(times(BoardCategory.activeBoardCategoriesForStocks().length * newStockCount))
                .persist(any(Board.class));
        }
    }
}
