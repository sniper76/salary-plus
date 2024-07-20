package ag.act.converter.stock;

import ag.act.converter.ActionLinkResponseConverter;
import ag.act.converter.Converter;
import ag.act.dto.MySolidarityDto;
import ag.act.dto.mysolidarity.InProgressActionUserStatus;
import ag.act.entity.Solidarity;
import ag.act.entity.SolidarityDailySummary;
import ag.act.entity.Stock;
import ag.act.entity.UserHoldingStock;
import ag.act.model.LinkResponse;
import ag.act.model.MySolidarityResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class MySolidarityResponseConverter implements Converter<MySolidarityDto, ag.act.model.MySolidarityResponse> {
    private final Integer minThresholdMemberCount;
    private final ActionLinkResponseConverter actionLinkResponseConverter;

    public MySolidarityResponseConverter(
        @Value("${solidarity.min-threshold-member-count:50}") final Integer minThresholdMemberCount,
        ActionLinkResponseConverter actionLinkResponseConverter
    ) {
        this.minThresholdMemberCount = minThresholdMemberCount;
        this.actionLinkResponseConverter = actionLinkResponseConverter;
    }

    @Override
    public MySolidarityResponse apply(MySolidarityDto mySolidarityDto) {
        return convert(mySolidarityDto);
    }

    public MySolidarityResponse convert(MySolidarityDto mySolidarityDto) {
        final Stock stock = mySolidarityDto.getStock();
        final Solidarity solidarity = mySolidarityDto.getSolidarity();
        final UserHoldingStock userHoldingStock = mySolidarityDto.getUserHoldingStock();
        final SolidarityDailySummary summary = Optional.ofNullable(mySolidarityDto.getMostRecentSolidarityDailySummary())
            .orElse(SolidarityDailySummary.createWithZeroValues());
        final InProgressActionUserStatus inProgressActionUserStatus = mySolidarityDto.getInProgressActionUserStatus();

        return new MySolidarityResponse()
            .status(solidarity.getStatus())
            .code(stock.getCode())
            .name(stock.getName())
            .stake(summary.getStake().floatValue())
            .stakeRank(mySolidarityDto.getStakeRank())
            .stakeRankDelta(mySolidarityDto.getStakeRankDelta())
            .marketValueRank(mySolidarityDto.getMarketValueRank())
            .marketValueRankDelta(mySolidarityDto.getMarketValueRankDelta())
            .memberCount(summary.getMemberCount())
            .requiredMemberCount(Math.max(0, minThresholdMemberCount - summary.getMemberCount()))
            .minThresholdMemberCount(minThresholdMemberCount)
            .links(convertLinks(inProgressActionUserStatus, stock))
            .displayOrder(userHoldingStock.getDisplayOrder());
    }

    private List<LinkResponse> convertLinks(InProgressActionUserStatus inProgressActionUserStatus, Stock stock) {
        return actionLinkResponseConverter.convert(
            inProgressActionUserStatus, stock.getCode()
        );
    }
}
