package ag.act.facade.batch.executor;

import ag.act.dto.BatchParameter;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.enums.DigitalDocumentType;
import ag.act.facade.batch.BatchRunner;
import ag.act.facade.batch.IBatchExecutor;
import ag.act.facade.digitaldocument.DigitalDocumentDownloadFacade;
import ag.act.service.digitaldocument.DigitalDocumentService;
import ag.act.util.KoreanDateTimeUtil;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional
public class CreateDigitalDocumentZipFilesBatchExecutor implements IBatchExecutor {
    private final BatchRunner batchRunner;
    private final EntityManager entityManager;
    private final DigitalDocumentService digitalDocumentService;
    private final DigitalDocumentDownloadFacade digitalDocumentDownloadFacade;

    @Override
    public boolean supports(BatchParameter batchParameter) {
        return BatchName.DIGITAL_DOCUMENT_ZIP_FILE.equals(batchParameter.getBatchName());
    }

    @Override
    public String execute(BatchParameter batchParameter) {
        final String date = getCurrentFormattedDateTime();
        final LocalDateTime startDateTime = KoreanDateTimeUtil.getTodayLocalDate().atStartOfDay();
        final LocalDateTime endDateTime = startDateTime.minusDays(1);
        final List<DigitalDocument> digitalDocuments = getDigitalDocuments(startDateTime, endDateTime);
        final int totalCount = digitalDocuments.size();
        final String batchName = batchParameter.getBatchName();

        return batchRunner.run(
            new BatchRunner.BatchRunnerParameter(batchParameter, date, totalCount, entityManager),
            (Consumer<Integer> batchCountLog) -> {
                final AtomicInteger count = new AtomicInteger(0);
                digitalDocuments.forEach(it -> {
                    digitalDocumentDownloadFacade.createDigitalDocumentZipFile(it.getId(), Boolean.TRUE);

                    count.incrementAndGet();
                    batchCountLog.accept(count.get());
                });

                return "[Batch] %s batch successfully finished. [requested: %s / %s]".formatted(batchName, count.get(), totalCount);
            }
        );
    }

    private List<DigitalDocument> getDigitalDocuments(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return digitalDocumentService.getDigitalDocuments(
            DigitalDocumentType.DIGITAL_PROXY,
            ag.act.model.Status.ACTIVE,
            startDateTime,
            endDateTime
        );
    }
}
