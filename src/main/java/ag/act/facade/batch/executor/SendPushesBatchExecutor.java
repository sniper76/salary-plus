package ag.act.facade.batch.executor;

import ag.act.dto.BatchParameter;
import ag.act.entity.Push;
import ag.act.facade.PushFacade;
import ag.act.facade.batch.BatchRunner;
import ag.act.facade.batch.IBatchExecutor;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Consumer;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional
public class SendPushesBatchExecutor implements IBatchExecutor {
    private final BatchRunner batchRunner;
    private final EntityManager entityManager;
    private final PushFacade pushFacade;

    @Override
    public boolean supports(BatchParameter batchParameter) {
        return BatchName.SEND_PUSHES.equals(batchParameter.getBatchName());
    }

    @Override
    public String execute(BatchParameter batchParameter) {
        final String date = getCurrentFormattedDateTime();
        final List<Push> pushList = pushFacade.getPushListToSend();
        final int totalCount = pushList.size();
        final String batchName = batchParameter.getBatchName();

        return batchRunner.run(
            new BatchRunner.BatchRunnerParameter(batchParameter, date, totalCount, entityManager),
            (Consumer<Integer> batchCountLog) -> {
                pushList.forEach(pushFacade::sendPush);

                return "[Batch] %s batch successfully finished. [sent: %s / %s]".formatted(batchName, totalCount, totalCount);
            }
        );
    }
}
