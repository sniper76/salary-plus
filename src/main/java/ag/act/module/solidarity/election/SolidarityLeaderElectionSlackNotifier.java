package ag.act.module.solidarity.election;

import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.entity.solidarity.election.SolidarityLeaderElection;
import ag.act.enums.SlackChannel;
import ag.act.service.stock.StockService;
import ag.act.util.SlackMessageSender;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Transactional
@Component
public class SolidarityLeaderElectionSlackNotifier {

    private final SlackMessageSender slackMessageSender;
    private final StockService stockService;

    public void notifyIfApplicable(SolidarityLeaderElection solidarityLeaderElection) {
        if (solidarityLeaderElection.isVotePeriodElection()) {
            notifyElectionStarted(solidarityLeaderElection.getStockCode());
            // TODO 히스토리를 정말 남겨야 할까?
        }

        if (solidarityLeaderElection.isFinishedElection()) {
            notifyElectionFinished(solidarityLeaderElection.getStockCode());
            // TODO 히스토리를 정말 남겨야 할까?
        }
    }

    public void notifyIfApplicantComplete(Long solidarityId, User user) {
        final Stock stock = stockService.getStock(solidarityId);

        sendSlackMessage("%s 주주대표 후보자 %s 지원".formatted(stock.getName(), user.getNickname()));
    }

    private void notifyElectionStarted(String stockCode) {
        final Stock stock = getStock(stockCode);
        sendSlackMessage("%s 주주대표 선출 투표 시작".formatted(stock.getName()));
    }

    private void notifyElectionFinished(String stockCode) {
        final Stock stock = getStock(stockCode);
        sendSlackMessage("%s 주주대표 선출 투표 종료".formatted(stock.getName()));
    }

    private void sendSlackMessage(String message) {
        slackMessageSender.sendSlackMessage(message, SlackChannel.ACT_SOLIDARITY_LEADER_APPLICANT_ALERT);
    }

    private Stock getStock(String stockCode) {
        return stockService.getStock(stockCode);
    }
}
