package ag.act.facade.batch.executor;

import ag.act.dto.BatchParameter;
import ag.act.facade.batch.BatchRunner;
import ag.act.facade.batch.IBatchExecutor;
import ag.act.module.dart.DartCorpCodeService;
import ag.act.module.dart.dto.CorpCodeItem;
import ag.act.module.dart.dto.CorpCodeResult;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional
public class UpdateCorpCodesBatchExecutor implements IBatchExecutor {
    private final BatchRunner batchRunner;
    private final EntityManager entityManager;
    private final DartCorpCodeService dartCorpCodeService;

    @Override
    public boolean supports(BatchParameter batchParameter) {
        return BatchName.UPDATE_DART_CORP_CODE.equals(batchParameter.getBatchName());
    }

    @Override
    public String execute(BatchParameter batchParameter) {
        final String date = getCurrentFormattedDateTime();
        final List<CorpCodeItem> corpCodeItems = getCorpCodeItems();
        final int totalCount = corpCodeItems.size();
        final String batchName = batchParameter.getBatchName();

        return batchRunner.run(
            new BatchRunner.BatchRunnerParameter(batchParameter, date, totalCount, entityManager),
            (Consumer<Integer> batchCountLog) -> {
                final AtomicInteger count = new AtomicInteger(0);
                corpCodeItems.forEach(corpCodeItem -> {
                    dartCorpCodeService.upsert(corpCodeItem);
                    count.incrementAndGet();
                });

                return "[Batch] %s batch successfully finished. [modification: %s / %s] on %s".formatted(batchName, count, totalCount, date);
            }
        );
    }

    @NotNull
    private List<CorpCodeItem> getCorpCodeItems() {
        final CorpCodeResult corpCodeResult = dartCorpCodeService.readCorpCodeResult();
        return corpCodeResult.getList().stream()
            .filter(corpCodeItem -> StringUtils.isNotBlank(corpCodeItem.getStockCode()))
            .toList();
    }
}
