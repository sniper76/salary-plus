package ag.act.service;


import ag.act.dto.BatchParameter;
import ag.act.entity.BatchLog;
import ag.act.enums.BatchStatus;
import ag.act.exception.BadRequestException;
import ag.act.exception.InternalServerException;
import ag.act.repository.BatchLogRepository;
import ag.act.util.BatchLogFinder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class BatchLogService {

    private static final int TEM_MINUTES = 10;
    private final BatchLogRepository batchLogRepository;

    private final BatchLogFinder batchLogFinder;

    private final Set<BatchStatus> notFailureBatchStatuses;

    @Autowired
    public BatchLogService(BatchLogRepository batchLogRepository, BatchLogFinder batchLogFinder) {
        this(batchLogRepository, batchLogFinder, BatchStatus.getNotFailureStatuses());
    }

    private BatchLogService(
        BatchLogRepository batchLogRepository,
        BatchLogFinder batchLogFinder,
        Set<BatchStatus> notFailureBatchStatuses
    ) {
        this.batchLogRepository = batchLogRepository;
        this.batchLogFinder = batchLogFinder;
        this.notFailureBatchStatuses = notFailureBatchStatuses;
    }

    public Optional<Long> logIfCanProceed(BatchParameter batchParameter) {
        final String batchName = batchParameter.getBatchName();
        final String batchGroupName = batchParameter.getBatchGroupName();
        final Integer batchPeriod = batchParameter.getBatchPeriod();
        final Optional<BatchLog> batchLogOptional = findBatchLogByBatchNameWithInBatchPeriod(batchParameter);

        if (batchLogOptional.isPresent() && !isFailure(batchLogOptional.get())) {
            return Optional.empty();
        }

        final BatchLog batchLog = new BatchLog();
        batchLog.setBatchName(batchName);
        batchLog.setBatchGroupName(batchGroupName);
        batchLog.setBatchPeriod(batchPeriod);
        batchLog.setBatchStatus(BatchStatus.IN_PROGRESS);
        batchLog.setPeriodTimeUnit(batchParameter.getPeriodTimeUnit());
        batchLog.setResult(null);
        batchLog.setStartTime(LocalDateTime.now());
        batchLog.setEndTime(null);

        final BatchLog savedLog = batchLogRepository.save(batchLog);

        return Optional.of(savedLog.getId());
    }

    public void logAfterReturning(Long batchLogId, Object result) {
        final Optional<BatchLog> batchLogOptional = findLatestBatchLogByBatchLogId(batchLogId);

        batchLogOptional.ifPresentOrElse(
            batchLog -> {
                batchLog.setEndTime(LocalDateTime.now());
                batchLog.setBatchStatus(BatchStatus.SUCCESS);
                batchLog.setResult(Objects.toString(result));

                batchLogRepository.save(batchLog);
            },
            () -> throwRuntimeException(batchLogId)
        );

    }

    public void logAfterThrowing(Long batchLogId, String failureReason) {
        final Optional<BatchLog> batchLogOptional = findLatestBatchLogByBatchLogId(batchLogId);

        batchLogOptional.ifPresentOrElse(
            batchLog -> {
                batchLog.setEndTime(LocalDateTime.now());
                batchLog.setResult(failureReason);
                batchLog.setBatchStatus(BatchStatus.FAILURE);

                batchLogRepository.save(batchLog);
            },
            () -> throwRuntimeException(batchLogId)
        );
    }

    private void throwRuntimeException(Long batchLogId) {
        final String errorMessage = "Batch Log not found with batchLogId(%s)".formatted(batchLogId);
        log.error(errorMessage);
        throw new BadRequestException(errorMessage);
    }

    private boolean isFailure(BatchLog batchLog) {
        return !notFailureBatchStatuses.contains(batchLog.getBatchStatus());
    }

    private Optional<BatchLog> findBatchLogByBatchNameWithInBatchPeriod(BatchParameter batchParameter) {
        return batchLogFinder.findBatchLogByBatchNameWithInBatchPeriod(batchParameter);
    }

    private Optional<BatchLog> findLatestBatchLogByBatchLogId(Long batchLogId) {
        return batchLogFinder.findLatestBatchLogByBatchLogId(batchLogId);
    }

    @SuppressWarnings("BusyWait")
    public void waitUntilSameGroupBatchIsFinishedExceptCurrentBatch(String batchName, String batchGroupName, Long batchLogId) {
        final long startTime = System.currentTimeMillis();

        while (batchLogFinder.findInProgressBatchInSameGroupExceptBatchLogId(batchGroupName, batchLogId).isPresent()) {
            try {
                log.info("Batch ({}) is waiting to finish another batch in the same group({})", batchName, batchGroupName);
                Thread.sleep(10_000);
            } catch (InterruptedException ex) {
                throw new InternalServerException(ex.getMessage(), ex);
            }
            if (System.currentTimeMillis() - startTime > 120_000) {
                throw new InternalServerException("Timeout waiting for %s group to run %s".formatted(batchGroupName, batchName));
            }
        }

    }

    public List<BatchLog> getUnfinishedBatchesForCleanup() {
        return batchLogRepository.findAllByBatchStatusAndStartTimeIsBefore(
            BatchStatus.IN_PROGRESS,
            LocalDateTime.now().minusMinutes(TEM_MINUTES)
        );
    }

    public void forceToFailBatchLog(BatchLog batchLog) {
        batchLog.setEndTime(LocalDateTime.now());
        batchLog.setBatchStatus(BatchStatus.FAILURE);
        batchLog.setResult("Forced to fail");

        batchLogRepository.save(batchLog);
    }
}
