package ag.act.service.stock;

import ag.act.entity.Stock;
import ag.act.entity.StockReferenceDate;
import ag.act.repository.StockReferenceDateRepository;
import ag.act.util.DateTimeUtil;
import ag.act.util.KoreanDateTimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RegularGeneralMeetingStockReferenceDateService {
    private static final Month REFERENCE_DATE_MONTH_STANDARD = Month.APRIL;

    private final StockReferenceDateRepository stockReferenceDateRepository;

    public StockReferenceDate createIfNotFound(Stock stock) {
        return get(stock)
            .orElseGet(() -> create(stock));
    }

    private StockReferenceDate create(Stock stock) {
        StockReferenceDate stockReferenceDate = new StockReferenceDate();
        stockReferenceDate.setStockCode(stock.getCode());
        stockReferenceDate.setReferenceDate(getStockReferenceDate());

        return stockReferenceDateRepository.saveAndFlush(stockReferenceDate);
    }

    private Optional<StockReferenceDate> get(Stock stock) {
        return stockReferenceDateRepository.findByStockCodeAndReferenceDate(stock.getCode(), getStockReferenceDate());
    }

    private LocalDate getStockReferenceDate() {
        final LocalDate todayLocalDate = KoreanDateTimeUtil.getTodayLocalDate();

        if (DateTimeUtil.isMonthBefore(todayLocalDate, REFERENCE_DATE_MONTH_STANDARD)) {
            return KoreanDateTimeUtil.getEndOfLastYearLocalDate();
        }

        return KoreanDateTimeUtil.getEndOfThisYearLocalDate();
    }
}
