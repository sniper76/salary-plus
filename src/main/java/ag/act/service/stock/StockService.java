package ag.act.service.stock;

import ag.act.dto.stock.GetStocksSearchDto;
import ag.act.dto.stock.SimpleStockDto;
import ag.act.entity.Stock;
import ag.act.exception.NotFoundException;
import ag.act.model.Status;
import ag.act.repository.StockRepository;
import ag.act.repository.interfaces.SimpleStock;
import ag.act.repository.interfaces.StockSearchResultItem;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional
@Service
public class StockService implements StockServiceValidator {

    private final StockRepository stockRepository;

    public Optional<Stock> findByCode(String code) {
        return stockRepository.findByCode(code);
    }

    public Stock getStock(String stockCode) {
        return findByCode(stockCode)
            .orElseThrow(() -> new NotFoundException(NOT_FOUND_MESSAGE));
    }

    public Stock getStock(Long solidarityId) {
        return stockRepository.findBySolidarityId(solidarityId)
            .orElseThrow(() -> new NotFoundException(NOT_FOUND_MESSAGE));
    }

    public Optional<SimpleStockDto> findSimpleStockByCode(String code) {
        return stockRepository.findSimpleStockByCode(code);
    }

    public SimpleStock getSimpleStock(String stockCode) {
        return findSimpleStockByCode(stockCode)
            .orElseThrow(() -> new NotFoundException(NOT_FOUND_MESSAGE));
    }

    public List<Stock> findAllByCodes(List<String> codes) {
        return stockRepository.findAllByCodeIn(codes);
    }

    public List<Stock> findByStandardCodes(List<String> standardCodes) {
        return stockRepository.findAllByStandardCodeIn(standardCodes);
    }

    public List<Stock> getMostRelatedTopTenStocksBySearchKeyword(String searchKeyword) {
        if (StringUtils.isBlank(searchKeyword)) {
            return stockRepository.findTop10ByStatusIn(List.of(Status.ACTIVE));
        }

        return stockRepository.findTop10ByNameContainingAndStatusIn(
            searchKeyword, List.of(Status.ACTIVE)
        );
    }

    public Stock create(Stock stock) {
        return stockRepository.save(stock);
    }

    public Page<StockSearchResultItem> getStocks(GetStocksSearchDto getStocksSearchDto) {
        final List<String> codes = getStocksSearchDto.getCodes();
        final PageRequest pageRequest = getStocksSearchDto.getPageRequest();

        if (getStocksSearchDto.getIsOnlyPrivateStocks()) {
            return stockRepository.findPrivateStocksByCodeIn(codes, pageRequest);
        }
        return stockRepository.findAllByCodeInOrderByStakeDesc(codes, pageRequest);
    }

    public List<Stock> findAllByStockGroupId(Long stockGroupId) {
        return stockRepository.findAllByStockGroupId(stockGroupId);
    }

    public List<SimpleStockDto> getAllSimpleStocks() {
        return stockRepository.findAllSimpleStocks(Sort.by(Sort.Direction.ASC, "name"));
    }

    public List<SimpleStockDto> findAllSimpleStocksWithoutTestStocks() {
        return stockRepository.findAllSimpleStocksWithoutTestStocks(Sort.by(Sort.Direction.ASC, "name"));
    }

    public List<Stock> findAllWithUnreadPosts(Long userId) {
        return stockRepository.findAllWithUnreadPosts(userId);
    }
}
