package ag.act.dto.stock;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@AllArgsConstructor
@Getter

public class GetStockStatisticsSearchPeriodDto {
    private LocalDate from;
    private LocalDate to;
}
