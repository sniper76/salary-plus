package ag.act.service.admin.stock;


import ag.act.dto.admin.StockRankingDto;
import ag.act.entity.admin.StockRanking;
import ag.act.repository.admin.StockRankingRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class StockRankingService {
    private final StockRankingRepository stockRankingRepository;

    public List<StockRankingDto> findStockRankingsByDate(LocalDate searchDate) {
        return stockRankingRepository.findStockRankingDtoListByDate(searchDate);
    }

    public List<StockRanking> getListByDate(LocalDate searchDate) {
        return stockRankingRepository.findAllByDate(searchDate);
    }

    public Map<String, StockRanking> getStockRankingMap(LocalDate searchDate) {
        return getListByDate(searchDate).stream()
            .collect(Collectors.toMap(StockRanking::getStockCode, Function.identity()));
    }

    public void createStockRanking(
        StockRankingDto stockRankingDto,
        Map<String, StockRanking> yesterdayStockRankingMap,
        Map<String, StockRanking> twoDaysAgoStockRankingMap
    ) {
        final Optional<StockRanking> yesterdayStockRanking = Optional.ofNullable(yesterdayStockRankingMap.get(stockRankingDto.getStockCode()));
        final Optional<StockRanking> twoDaysAgoStockRanking = Optional.ofNullable(twoDaysAgoStockRankingMap.get(stockRankingDto.getStockCode()));

        final StockRanking stockRanking = stockRankingDto.toStockRanking(twoDaysAgoStockRanking);
        stockRanking.setId(yesterdayStockRanking.map(StockRanking::getId).orElse(null));
        stockRanking.setCreatedAt(yesterdayStockRanking.map(StockRanking::getCreatedAt).orElse(null));

        stockRankingRepository.save(stockRanking);
    }

    public List<StockRanking> findTopNStockRankings(PageRequest pageRequest) {
        return stockRankingRepository.findStockRankingTopN(pageRequest);
    }
}
