package ag.act.dto;

import ag.act.entity.SolidarityDailySummary;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Builder
public class RecentSolidarityDailySummaryDto {
    private Long solidarityId;
    private SolidarityDailySummary recentSummary;

    public RecentSolidarityDailySummaryDto(Long solidarityId, SolidarityDailySummary recentSummary) {
        this.solidarityId = solidarityId;
        this.recentSummary = recentSummary;
    }
}
