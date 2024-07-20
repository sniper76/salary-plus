package ag.act.service.stock;

import ag.act.entity.Board;
import ag.act.entity.Solidarity;
import ag.act.entity.Stock;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.exception.InternalServerException;
import ag.act.module.krx.KrxStockAggregator;
import ag.act.module.krx.KrxStockMapper;
import ag.act.service.BoardService;
import ag.act.service.IBatchProcessor;
import ag.act.service.solidarity.SolidarityService;
import ag.act.util.ChunkUtil;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Component
@Transactional
public class UpdateStockBatchProcessor implements IBatchProcessor {
    private final ChunkUtil chunkUtil;
    private final TestStockService testStockService;
    private final PrivateStockService privateStockService;
    private final KrxStockAggregator krxStockAggregator;
    private final StockService stockService;
    private final SolidarityService solidarityService;
    private final RegularGeneralMeetingStockReferenceDateService regularGeneralMeetingStockReferenceDateService;
    private final BoardService boardService;
    private final KrxStockMapper krxStockMapper;
    private final EntityManager entityManager;
    private final StockChangeChecker stockChangeChecker;

    public List<Stock> getSourceStocks(String date) {
        return Stream.of(
                getStocksFromKrxService(date).stream(),
                testStockService.getStocks().stream(),
                privateStockService.getActiveStocks().stream()
            )
            .flatMap(Function.identity())
            .toList();
    }

    private List<Stock> getStocksFromKrxService(String date) {
        try {
            return krxStockAggregator.getStocksFromKrxService(date);
        } catch (Exception e) {
            throw new InternalServerException("[Batch] KRX 서버에서 데이터를 가져오는 중에 오류가 발생하였습니다.", e);
        }
    }

    public void updateStocks(
        List<Stock> sourceStocks,
        BatchProcessorParameters batchProcessorParameters
    ) {
        final Consumer<Integer> batchCountLog = batchProcessorParameters.batchCountLog();
        final AtomicInteger updateCount = batchProcessorParameters.updateCount();
        final AtomicInteger createCount = batchProcessorParameters.createCount();
        final int chunkSize = batchProcessorParameters.chunkSize();

        chunkStocks(sourceStocks, chunkSize).forEach(stockChunk -> {
            final Map<String, Stock> sourceStocksMap = getSourceStocksMap(stockChunk);
            final List<Stock> stocksInDatabase = getStocksInDatabase(stockChunk);
            final List<Stock> stocksNotInDatabase = getStocksNotInDatabase(stockChunk, stocksInDatabase);

            stocksInDatabase.forEach(stockInDatabase -> {
                final Stock sourceStock = sourceStocksMap.get(stockInDatabase.getCode());
                stockChangeChecker.checkStockTotalIssuedQuantity(sourceStock, stockInDatabase);

                upsertStockAndRelatedData(
                    krxStockMapper.mergeStocks(sourceStock, stockInDatabase)
                );
                updateCount.incrementAndGet();
                batchCountLog.accept(updateCount.get() + createCount.get());
            });

            stocksNotInDatabase.forEach(stock -> {
                upsertStockAndRelatedData(stock);
                createCount.incrementAndGet();
                batchCountLog.accept(updateCount.get() + createCount.get());
            });
        });
    }

    private List<List<Stock>> chunkStocks(List<Stock> tokens, int chunkSize) {
        return chunkUtil.getChunks(tokens, chunkSize);
    }

    private Map<String, Stock> getSourceStocksMap(List<Stock> sourceStocks) {
        return sourceStocks.stream().collect(Collectors.toMap(Stock::getCode, Function.identity()));
    }

    private List<Stock> getStocksNotInDatabase(List<Stock> sourceStocks, List<Stock> stocksInDatabase) {
        final Map<String, Stock> foundStocksMap = stocksInDatabase.stream()
            .collect(Collectors.toMap(Stock::getCode, Function.identity()));

        return sourceStocks.stream()
            .filter(stock -> !foundStocksMap.containsKey(stock.getCode()))
            .toList();
    }

    private List<Stock> getStocksInDatabase(List<Stock> sourceStocks) {
        return stockService.findAllByCodes(
            sourceStocks.stream()
                .map(Stock::getCode)
                .toList()
        );
    }

    private void upsertStockAndRelatedData(Stock stock) {
        entityManager.persist(stock);

        createSolidarityIfNotFound(stock);
        createBoardsForAllCategories(stock);
        createRegularGeneralMeetingStockReferenceDateIfNotFound(stock);
    }

    private void createRegularGeneralMeetingStockReferenceDateIfNotFound(Stock stock) {
        regularGeneralMeetingStockReferenceDateService.createIfNotFound(stock);
    }

    private void createSolidarityIfNotFound(Stock stock) {
        if (stock.getSolidarity() != null) {
            return;
        }

        solidarityService.findSolidarity(stock.getCode())
            .orElseGet(() -> createSolidarity(stock));
    }

    private Solidarity createSolidarity(Stock stock) {
        final Solidarity solidarity = new Solidarity();
        solidarity.setStockCode(stock.getCode());
        solidarity.setStatus(ag.act.model.Status.ACTIVE);

        entityManager.persist(solidarity);

        stock.setSolidarityId(solidarity.getId());

        return solidarity;
    }

    private void createBoardsForAllCategories(Stock stock) {

        final Set<BoardCategory> boardCategorySetOfStock = getBoardCategoryBoardMap(stock);

        Arrays.stream(allActiveBoardCategories())
            .filter(boardCategory -> !boardCategorySetOfStock.contains(boardCategory))
            .forEach(boardCategory -> createBoard(stock, boardCategory));
    }

    private BoardCategory[] allActiveBoardCategories() {
        return BoardCategory.activeBoardCategoriesForStocks();
    }

    private void createBoard(Stock stock, BoardCategory boardCategory) {
        try {
            createBoard(stock, boardCategory.getBoardGroup(), boardCategory);
        } catch (Exception e) {
            throw new InternalServerException(
                "[Batch] 게시판을 생성하는 중에 오류가 발생하였습니다. stockCode=%s, boardGroup=%s, boardCategory=%s"
                    .formatted(stock.getCode(), boardCategory.getBoardGroup(), boardCategory), e);
        }
    }

    private void createBoard(Stock stock, BoardGroup boardGroup, BoardCategory boardCategory) {
        final Board board = new Board();

        board.setStock(stock);
        board.setStockCode(stock.getCode());
        board.setGroup(boardGroup);
        board.setCategory(boardCategory);
        board.setStatus(ag.act.model.Status.ACTIVE);

        entityManager.persist(board);
    }

    private Set<BoardCategory> getBoardCategoryBoardMap(Stock stock) {
        return boardService.getBoards(stock.getCode())
            .stream()
            .map(Board::getCategory)
            .collect(Collectors.toSet());
    }
}
