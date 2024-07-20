package ag.act.entity;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CalculateStake {
    private Long totalIssuedStockQuantity;
    private Long totalSolidarityMemberStockQuantity;

    public Double calculate() {
        return totalSolidarityMemberStockQuantity * 100.0 / totalIssuedStockQuantity;
    }
}
