package ag.act.facade.batch.executor;

import ag.act.dto.BatchParameter;
import ag.act.entity.digitaldocument.DigitalDocumentUser;
import ag.act.facade.batch.BatchRunner;
import ag.act.facade.batch.IBatchExecutor;
import ag.act.service.digitaldocument.DigitalDocumentUserService;
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
public class CleanupUnfinishedDigitalDocumentUsersBatchExecutor implements IBatchExecutor {
    private final BatchRunner batchRunner;
    private final EntityManager entityManager;
    private final DigitalDocumentUserService digitalDocumentUserService;

    @Override
    public boolean supports(BatchParameter batchParameter) {
        return BatchName.CLEANUP_UNFINISHED_DIGITAL_DOCUMENT_USERS.equals(batchParameter.getBatchName());
    }

    @Override
    public String execute(BatchParameter batchParameter) {
        final String date = getCurrentFormattedDateTime();
        final List<DigitalDocumentUser> digitalDocumentUsers = digitalDocumentUserService.getUnfinishedDigitalDocumentUsersForCleanup();
        final int totalCount = digitalDocumentUsers.size();
        final String batchName = batchParameter.getBatchName();

        return batchRunner.run(
            new BatchRunner.BatchRunnerParameter(batchParameter, date, totalCount, entityManager),
            (Consumer<Integer> batchCountLog) -> {
                final AtomicInteger count = new AtomicInteger(0);

                digitalDocumentUsers.forEach(digitalDocumentUser -> {
                    try {
                        digitalDocumentUserService.deleteUserDigitalDocument(digitalDocumentUser);
                        count.incrementAndGet();
                        batchCountLog.accept(count.get());
                    } catch (Exception e) {
                        log.error("[Batch] UnfinishedDigitalDocumentUser 삭제 중에 오류가 발생하였습니다. digitalDocumentUser={}", digitalDocumentUser, e);
                    }
                });

                return "[Batch] %s batch successfully finished. [deletion: %s / %s]".formatted(batchName, count.get(), totalCount);
            }
        );
    }
}
