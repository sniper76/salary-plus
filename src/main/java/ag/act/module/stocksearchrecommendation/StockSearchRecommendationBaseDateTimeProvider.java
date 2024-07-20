package ag.act.module.stocksearchrecommendation;

import ag.act.entity.admin.StockRanking;
import ag.act.util.DateTimeFormatUtil;
import ag.act.util.KoreanDateTimeUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class StockSearchRecommendationBaseDateTimeProvider {

    private static final long DAY_OFFSET_FOR_TODAY_BASED_ON_YESTERDAY_DATA = 1L;
    private static final String BASE_DATE_TIME_MESSAGE_TEMPLATE = "%s 기준";

    public String getBaseDateTimeForSearchTrend() {
        return BASE_DATE_TIME_MESSAGE_TEMPLATE.formatted(
            KoreanDateTimeUtil.getNowInKoreanTime().format(getFormatter())
        );
    }

    public String getBaseDateTimeForStockRanking(List<StockRanking> stockRankings) {
        return BASE_DATE_TIME_MESSAGE_TEMPLATE.formatted(
            extractBaseDateTime(stockRankings).format(getFormatter())
        );
    }

    private LocalDateTime extractBaseDateTime(List<StockRanking> stockRankings) {
        return stockRankings.stream()
            .findFirst()
            .map(StockRanking::getDate)
            .orElseGet(KoreanDateTimeUtil::getYesterdayLocalDate)
            .atStartOfDay()
            .plusDays(DAY_OFFSET_FOR_TODAY_BASED_ON_YESTERDAY_DATA);
    }

    @NotNull
    private DateTimeFormatter getFormatter() {
        return DateTimeFormatUtil.yyyy_MM_dd_HH_mm();
    }
}
