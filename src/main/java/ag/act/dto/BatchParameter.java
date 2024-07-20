package ag.act.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class BatchParameter {
    private String batchName;
    private String batchGroupName;
    private Integer batchPeriod;
    private ag.act.model.BatchRequest.PeriodTimeUnitEnum periodTimeUnit;
    private Boolean isFirstCreateUserHoldingStockHistory;
}
