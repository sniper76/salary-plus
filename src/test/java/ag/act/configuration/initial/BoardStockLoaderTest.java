package ag.act.configuration.initial;

import ag.act.core.configuration.GlobalBoardManager;
import ag.act.entity.Board;
import ag.act.entity.Stock;
import ag.act.enums.BoardCategory;
import ag.act.service.BoardService;
import ag.act.service.stock.StockService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class BoardStockLoaderTest {
    @InjectMocks
    private BoardStockLoader loader;
    @Mock
    private GlobalBoardManager globalBoardManager;
    @Mock
    private StockService stockService;
    @Mock
    private BoardService boardService;
    private String boardStockCode;
    @Mock
    private Stock stock;
    private int boardCategorySize;

    @BeforeEach
    void setUp() {
        boardStockCode = someString(6);
        boardCategorySize = getBoardCategories().size();

        given(globalBoardManager.getStockCode()).willReturn(boardStockCode);
        given(stockService.create(any(Stock.class))).willReturn(stock);
        given(stock.getCode()).willReturn(boardStockCode);

        getBoardCategories().forEach(
            boardCategory -> given(boardService.findBoard(boardStockCode, boardCategory))
                .willReturn(Optional.empty())
        );
    }

    @Nested
    class Load {

        @Nested
        class CreateAllBoardsSuccessfully {

            @Nested
            class WhenStockIsNotExists extends DefaultTestCases {
                @BeforeEach
                void setUp() {
                    given(stockService.findByCode(boardStockCode)).willReturn(Optional.empty());

                    loader.load();
                }

                @Test
                void shouldCreateStock() {
                    then(stockService).should().create(any(Stock.class));
                }
            }

            @Nested
            class WhenStockIsAlreadyExists extends DefaultTestCases {
                @BeforeEach
                void setUp() {
                    given(stockService.findByCode(boardStockCode)).willReturn(Optional.of(stock));

                    loader.load();
                }

                @Test
                void shouldNotCreateBoardStock() {
                    then(stockService).should(never()).create(any(Stock.class));
                }
            }

            @Nested
            class WhenSomeBoardAreAlreadyExists {

                @BeforeEach
                void setUp() {
                    getBoardCategories().forEach(
                        boardCategory -> given(boardService.findBoard(boardStockCode, boardCategory))
                            .willReturn(Optional.of(mock(Board.class)))
                    );

                    loader.load();
                }

                @Test
                void shouldCreateStock() {
                    then(stockService).should().create(any(Stock.class));
                }

                @Test
                void shouldNotCreateBoards() {
                    then(boardService).should(never()).create(any(Board.class));
                }
            }
        }

        @SuppressWarnings("unused")
        class DefaultTestCases {

            @Test
            void shouldCreateBoards() {
                then(boardService).should(times(boardCategorySize)).create(any(Board.class));
            }
        }
    }

    private List<BoardCategory> getBoardCategories() {
        return Arrays.stream(BoardCategory.activeBoardCategoriesForGlobalBoard()).toList();
    }
}