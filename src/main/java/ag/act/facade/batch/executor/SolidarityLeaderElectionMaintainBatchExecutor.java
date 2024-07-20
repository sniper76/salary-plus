package ag.act.facade.batch.executor;

import ag.act.dto.BatchParameter;
import ag.act.entity.solidarity.election.SolidarityLeaderElection;
import ag.act.facade.batch.BatchRunner;
import ag.act.facade.batch.IBatchExecutor;
import ag.act.module.solidarity.election.SolidarityLeaderElectionMaintainer;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional
public class SolidarityLeaderElectionMaintainBatchExecutor implements IBatchExecutor {
    private final BatchRunner batchRunner;
    private final EntityManager entityManager;
    private final SolidarityLeaderElectionMaintainer solidarityLeaderElectionMaintainer;

    @Override
    public boolean supports(BatchParameter batchParameter) {
        return BatchName.MAINTAIN_SOLIDARITY_LEADER_ELECTIONS.equals(batchParameter.getBatchName());
    }

    @Override
    public String execute(BatchParameter batchParameter) {
        final String date = getCurrentFormattedDateTime();
        final List<SolidarityLeaderElection> allActiveSolidarityLeaderElections = getAllActiveSolidarityLeaderElections();
        final int totalCount = allActiveSolidarityLeaderElections.size();
        final String batchName = batchParameter.getBatchName();

        return batchRunner.run(
            new BatchRunner.BatchRunnerParameter(batchParameter, date, totalCount, entityManager),
            (Consumer<Integer> batchCountLog) -> {
                final AtomicInteger count = new AtomicInteger(0);

                allActiveSolidarityLeaderElections.forEach(solidarityLeaderElection -> {
                    try {
                        solidarityLeaderElectionMaintainer.maintainSolidarityLeaderElection(solidarityLeaderElection);
                        count.incrementAndGet();
                        batchCountLog.accept(count.get());
                    } catch (Exception e) {
                        logErrorElection(solidarityLeaderElection, e);
                    }
                });

                return "[Batch] %s batch successfully finished. [finished: %s / %s] on %s".formatted(batchName, count.get(), totalCount, date);
            }
        );
    }

    private List<SolidarityLeaderElection> getAllActiveSolidarityLeaderElections() {
        return solidarityLeaderElectionMaintainer.getAllActiveSolidarityLeaderElections();
    }

    private void logErrorElection(SolidarityLeaderElection solidarityLeaderElection, Exception exception) {
        log.error(
            "[Batch] {} 처리 중에 오류가 발생하였습니다, [solidarityLeaderElectionId: {}, stockCode: {}]",
            BatchName.MAINTAIN_SOLIDARITY_LEADER_ELECTIONS,
            solidarityLeaderElection.getId(),
            solidarityLeaderElection.getStockCode(),
            exception
        );
    }
}
