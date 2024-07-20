package ag.act.facade.batch.executor;

import ag.act.dto.BatchParameter;
import ag.act.entity.User;
import ag.act.facade.batch.BatchRunner;
import ag.act.facade.batch.IBatchExecutor;
import ag.act.facade.user.UserFacade;
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
public class UpdateWithdrawalRequestUsersBatchExecutor implements IBatchExecutor {
    private final BatchRunner batchRunner;
    private final EntityManager entityManager;
    private final UserFacade userFacade;

    @Override
    public boolean supports(BatchParameter batchParameter) {
        return BatchName.UPDATE_WITHDRAWAL_REQUEST_USERS.equals(batchParameter.getBatchName());
    }

    @Override
    public String execute(BatchParameter batchParameter) {
        final String date = getCurrentFormattedDateTime();
        final List<User> withdrawRequestedUsers = userFacade.getWithdrawRequestedUsersBeforeOneDay();
        final int totalCount = withdrawRequestedUsers.size();
        final String batchName = batchParameter.getBatchName();

        return batchRunner.run(
            new BatchRunner.BatchRunnerParameter(batchParameter, date, totalCount, entityManager),
            (Consumer<Integer> batchCountLog) -> {
                final AtomicInteger count = new AtomicInteger(0);
                withdrawRequestedUsers.forEach(user -> {
                    userFacade.withdrawUser(user, ag.act.model.Status.DELETED_BY_USER);
                    count.incrementAndGet();
                    batchCountLog.accept(count.get());
                });

                return "[Batch] %s batch successfully finished. [deletion: %s / %s]".formatted(batchName, count.get(), totalCount);
            }
        );
    }
}
