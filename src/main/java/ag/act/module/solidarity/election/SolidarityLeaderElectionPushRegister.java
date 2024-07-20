package ag.act.module.solidarity.election;

import ag.act.entity.Push;
import ag.act.entity.Stock;
import ag.act.entity.solidarity.election.SolidarityLeaderElection;
import ag.act.enums.AppLinkType;
import ag.act.enums.push.PushSendType;
import ag.act.enums.push.PushTargetType;
import ag.act.model.CreatePushRequest;
import ag.act.service.push.PushService;
import ag.act.service.stock.StockService;
import ag.act.service.stockboardgrouppost.PostPushTargetDateTimeManager;
import ag.act.util.DateTimeUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Transactional
@Component
public class SolidarityLeaderElectionPushRegister {
    private final StockService stockService;
    private final PushService pushService;
    private final PostPushTargetDateTimeManager postPushTargetDateTimeManager;
    private final SolidarityLeaderElectionPushUnregister solidarityLeaderElectionPushUnregister;

    public void register(SolidarityLeaderElection solidarityLeaderElection) {

        if (!solidarityLeaderElection.isPendingElection()) {
            unregisterPush(solidarityLeaderElection);
        }

        if (solidarityLeaderElection.isCandidateRegistrationPeriodElection()) {
            registerPushForCandidateRegistrationStarted(solidarityLeaderElection);
            // TODO 히스토리를 정말 남겨야 할까?
        }

        if (solidarityLeaderElection.isVotePeriodElection()) {
            registerPushForElectionStarted(solidarityLeaderElection);
            // TODO 히스토리를 정말 남겨야 할까?
        }

        if (solidarityLeaderElection.isFinishedElection()) {
            registerPushForElectionFinished(solidarityLeaderElection);
            // TODO 히스토리를 정말 남겨야 할까?
        }
    }

    private void unregisterPush(SolidarityLeaderElection solidarityLeaderElection) {
        solidarityLeaderElectionPushUnregister.unregister(solidarityLeaderElection);
    }

    private Push registerPushForElectionStarted(SolidarityLeaderElection solidarityLeaderElection) {
        final Stock stock = stockService.getStock(solidarityLeaderElection.getStockCode());
        return pushService.createPush(
            generateCreatePushRequest(
                solidarityLeaderElection.getPostId(),
                stock.getCode(),
                "%s 주주대표 선출 절차".formatted(stock.getName()),
                "%s 주주대표 선출 투표가 시작되었습니다. 내가 원하는 주주대표에 투표를 해 주세요!".formatted(stock.getName())
            )
        );
    }

    private Push registerPushForElectionFinished(SolidarityLeaderElection solidarityLeaderElection) {
        final Stock stock = stockService.getStock(solidarityLeaderElection.getStockCode());
        return pushService.createPush(
            generateCreatePushRequest(
                solidarityLeaderElection.getPostId(),
                stock.getCode(),
                "%s 주주대표 선출 투표 종료".formatted(stock.getName()),
                "%s 주주대표 선출 투표가 종료되었습니다. 지금 바로 결과를 확인해 보세요!".formatted(stock.getName())
            )
        );
    }

    private Push registerPushForCandidateRegistrationStarted(SolidarityLeaderElection solidarityLeaderElection) {
        final Stock stock = stockService.getStock(solidarityLeaderElection.getStockCode());
        return pushService.createPush(
            generateCreatePushRequest(
                solidarityLeaderElection.getPostId(),
                stock.getCode(),
                "%s 주주대표 선출 절차".formatted(stock.getName()),
                "%s 주주대표 선출 절차가 시작되었습니다. 후보자들의 공약을 살펴보고, 주주대표에 관심 있다면 직접 지원해보세요.".formatted(stock.getName())
            )
        );
    }

    private CreatePushRequest generateCreatePushRequest(Long postId, String stockCode, String pushTitle, String pushContent) {
        return new CreatePushRequest()
            .title(pushTitle)
            .content(pushContent)
            .postId(postId)
            .linkType(AppLinkType.STOCK_HOME.name())
            .stockTargetType(PushTargetType.STOCK.name())
            .stockCode(stockCode)
            .sendType(PushSendType.SCHEDULE.name())
            .targetDatetime(postPushTargetDateTimeManager.generatePushTargetDateTime(DateTimeUtil.getTodayInstant()))
            ;
    }
}
