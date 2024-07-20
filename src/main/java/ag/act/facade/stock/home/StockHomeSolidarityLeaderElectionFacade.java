package ag.act.facade.stock.home;

import ag.act.configuration.security.ActUserProvider;
import ag.act.converter.election.LeaderElectionDetailResponseConverter;
import ag.act.converter.stock.SolidarityLeaderApplicationResponseForStockHomeConverter;
import ag.act.entity.User;
import ag.act.model.LeaderElectionDetailResponse;
import ag.act.model.SolidarityLeaderApplicationResponse;
import ag.act.service.solidarity.election.SolidarityLeaderElectionService;
import ag.act.service.stock.home.StockHomeSolidarityLeaderElectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class StockHomeSolidarityLeaderElectionFacade {
    private final SolidarityLeaderElectionService solidarityLeaderElectionService;
    private final StockHomeSolidarityLeaderElectionService stockHomeSolidarityLeaderElectionService;
    private final SolidarityLeaderApplicationResponseForStockHomeConverter solidarityLeaderApplicationResponseForStockHomeConverter;
    private final LeaderElectionDetailResponseConverter leaderElectionDetailResponseConverter;

    public SolidarityLeaderApplicationResponse getLeaderApplication(String stockCode) {
        final User user = ActUserProvider.getNoneNull();

        return stockHomeSolidarityLeaderElectionService.findLeaderApplication(user.getId(), stockCode)
            .map(solidarityLeaderApplicationResponseForStockHomeConverter::convert)
            .orElse(null);
    }

    public LeaderElectionDetailResponse getLeaderElectionDetail(String stockCode) {
        return solidarityLeaderElectionService.findVoteClosingDateTimePlusOneDaySolidarityLeaderElection(stockCode)
            .map(leaderElectionDetailResponseConverter::convert)
            .orElse(null);
    }
}
