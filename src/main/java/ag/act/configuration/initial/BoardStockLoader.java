package ag.act.configuration.initial;

import ag.act.core.configuration.GlobalBoardManager;
import ag.act.entity.Board;
import ag.act.entity.Stock;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.service.BoardService;
import ag.act.service.stock.StockService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@Transactional
public class BoardStockLoader implements InitialLoader {

    private final GlobalBoardManager globalBoardManager;
    private final StockService stockService;
    private final BoardService boardService;

    public BoardStockLoader(
        GlobalBoardManager globalBoardManager,
        StockService stockService,
        BoardService boardService
    ) {
        this.globalBoardManager = globalBoardManager;
        this.stockService = stockService;
        this.boardService = boardService;
    }

    @Override
    public void load() {
        createBoards(getOrCreateStock());
    }

    private Stock getOrCreateStock() {
        return stockService
            .findByCode(globalBoardManager.getStockCode())
            .orElseGet(this::createBoardStock);
    }

    private Stock createBoardStock() {

        final String stockCodeOfGlobalBoard = globalBoardManager.getStockCode();

        Stock stock = new Stock();
        stock.setName("주주행동 News");
        stock.setCode(stockCodeOfGlobalBoard);
        stock.setStandardCode("ACT" + stockCodeOfGlobalBoard + "999");
        stock.setStatus(ag.act.model.Status.ACTIVE);
        stock.setFullName(stockCodeOfGlobalBoard + "게시판");
        stock.setMarketType("CONDUIT");
        stock.setStockType("액트주");
        stock.setClosingPrice(10_000);
        stock.setTotalIssuedQuantity(1_000_000L);

        return stockService.create(stock);
    }

    private void createBoards(Stock stock) {
        for (BoardCategory boardCategory : getGlobalBoardCategories()) {
            try {
                final Optional<Board> board = boardService.findBoard(stock.getCode(), boardCategory);
                if (board.isEmpty()) {
                    createBoard(stock, boardCategory.getBoardGroup(), boardCategory);
                }
            } catch (Exception e) {
                // ignore
                log.error("게시판을 생성하는 중에 오류가 발생하였습니다. stockCode={}, boardGroup={}, boardCategory={}",
                    stock.getCode(), boardCategory.getBoardGroup(), boardCategory, e);
            }
        }
    }

    private List<BoardCategory> getGlobalBoardCategories() {
        return BoardGroup.getGlobalBoardGroups()
            .stream()
            .map(BoardGroup::getCategories)
            .flatMap(List::stream)
            .toList();
    }

    private void createBoard(Stock stock, BoardGroup boardGroup, BoardCategory boardCategory) {
        final Board board = new Board();
        board.setStock(stock);
        board.setStockCode(stock.getCode());
        board.setGroup(boardGroup);
        board.setCategory(boardCategory);
        board.setStatus(ag.act.model.Status.ACTIVE);

        boardService.create(board);
    }
}
