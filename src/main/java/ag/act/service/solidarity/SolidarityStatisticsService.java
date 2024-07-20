package ag.act.service.solidarity;

import ag.act.dto.stock.GetStockStatisticsSearchDto;
import ag.act.enums.admin.StockStatisticsPeriodType;
import ag.act.repository.SolidarityDailyStatisticsRepository;
import ag.act.repository.interfaces.StockStatisticsResultItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SolidarityStatisticsService {

    private final SolidarityDailyStatisticsRepository solidarityDailyStatisticsRepository;

    public List<StockStatisticsResultItem> getSolidarityStatistics(GetStockStatisticsSearchDto getStockStatisticsSearchDto) {

        if (StockStatisticsPeriodType.DAILY == getStockStatisticsSearchDto.getPeriodType()) {
            return solidarityDailyStatisticsRepository.findDailyStatisticsBy(
                getStockStatisticsSearchDto.getCode(),
                getStockStatisticsSearchDto.getSearchPeriod().getFrom(),
                getStockStatisticsSearchDto.getSearchPeriod().getTo()
            );
        }

        return solidarityDailyStatisticsRepository.findMonthlyStatisticsBy(
            getStockStatisticsSearchDto.getCode(),
            getStockStatisticsSearchDto.getSearchPeriod().getFrom(),
            getStockStatisticsSearchDto.getSearchPeriod().getTo()
        );
    }
}
