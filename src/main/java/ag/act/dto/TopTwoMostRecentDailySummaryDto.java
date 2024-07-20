package ag.act.dto;

import ag.act.entity.SolidarityDailySummary;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TopTwoMostRecentDailySummaryDto {
    private SolidarityDailySummary mostRecentSummary;
    private SolidarityDailySummary secondMostRecentSummary;
}
