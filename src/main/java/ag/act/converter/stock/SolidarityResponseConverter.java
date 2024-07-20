package ag.act.converter.stock;

import ag.act.entity.Solidarity;
import ag.act.entity.SolidarityDailySummary;
import ag.act.entity.Stock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SolidarityResponseConverter {
    private final Integer minThresholdMemberCount;

    public SolidarityResponseConverter(
        @Value("${solidarity.min-threshold-member-count:50}") final Integer minThresholdMemberCount
    ) {
        this.minThresholdMemberCount = minThresholdMemberCount;
    }

    public ag.act.model.SolidarityResponse convert(Solidarity solidarity) {
        final Stock stock = solidarity.getStock();
        final SolidarityDailySummary summary = Optional.ofNullable(solidarity.getMostRecentDailySummary())
            .orElse(SolidarityDailySummary.createWithZeroValues());

        return new ag.act.model.SolidarityResponse()
            .status(solidarity.getStatus())
            .id(solidarity.getId())
            .code(stock.getCode())
            .name(stock.getName())
            .representativePhoneNumber(stock.getRepresentativePhoneNumber())
            .stake(summary.getStake().floatValue())
            .memberCount(summary.getMemberCount())
            .minThresholdMemberCount(minThresholdMemberCount)
            .requiredMemberCount(Math.max(0, minThresholdMemberCount - summary.getMemberCount()));
    }
}
