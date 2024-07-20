package ag.act.enums.admin;

import ag.act.dto.stock.GetStockStatisticsSearchPeriodDto;
import ag.act.exception.BadRequestException;
import ag.act.parser.DateTimeParser;
import lombok.Getter;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Getter
public enum StockStatisticsPeriodType {
    DAILY("일별") {
        @Override
        public GetStockStatisticsSearchPeriodDto getSearchPeriod(String period) {
            try {
                final LocalDate periodFrom = DateTimeParser.parseDate(period + "01").toLocalDate();
                final LocalDate periodTo = periodFrom.plusMonths(1).minusDays(1);
                return new GetStockStatisticsSearchPeriodDto(periodFrom, periodTo);
            } catch (DateTimeParseException e) {
                throw new BadRequestException("검색할 period 를 'YYYYMM' 형식으로 입력해주세요.");
            }
        }
    },
    MONTHLY("월별") {
        @Override
        public GetStockStatisticsSearchPeriodDto getSearchPeriod(String period) {
            try {
                final LocalDate periodFrom = DateTimeParser.parseDate(period + "0101").toLocalDate();
                final LocalDate periodTo = periodFrom.plusYears(1).minusDays(1);
                return new GetStockStatisticsSearchPeriodDto(periodFrom, periodTo);
            } catch (DateTimeParseException e) {
                throw new BadRequestException("검색할 period 를 'YYYY' 형식으로 입력해주세요.");
            }
        }
    };

    private final String displayName;

    StockStatisticsPeriodType(String displayName) {
        this.displayName = displayName;
    }

    public abstract GetStockStatisticsSearchPeriodDto getSearchPeriod(String period);

    public static StockStatisticsPeriodType fromValue(String searchTypeName) {
        try {
            return StockStatisticsPeriodType.valueOf(searchTypeName.toUpperCase());
        } catch (NullPointerException | IllegalArgumentException e) {
            return DAILY;
        }
    }
}
