package ag.act.util;

import ag.act.dto.BatchParameter;
import ag.act.entity.BatchLog;
import ag.act.enums.BatchStatus;
import ag.act.model.BatchRequest;
import ag.act.repository.BatchLogRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class BatchLogFinder {
    private final BatchLogRepository batchLogRepository;

    public BatchLogFinder(BatchLogRepository batchLogRepository) {
        this.batchLogRepository = batchLogRepository;
    }

    public Optional<BatchLog> findLatestBatchLogByBatchLogId(Long batchLogId) {
        return batchLogRepository.findById(batchLogId);
    }

    public Optional<BatchLog> findBatchLogByBatchNameWithInBatchPeriod(BatchParameter batchParameter) {

        final String batchName = batchParameter.getBatchName();
        final Integer batchPeriod = batchParameter.getBatchPeriod();
        final BatchRequest.PeriodTimeUnitEnum periodTimeUnit = batchParameter.getPeriodTimeUnit();

        final LocalDateTime targetStartTime = periodTimeUnit == BatchRequest.PeriodTimeUnitEnum.HOUR
            ? DateTimeUtil.getCurrentDateTimeMinusHours(batchPeriod).plusMinutes(10L)
            : DateTimeUtil.getCurrentDateTimeMinusMinutes(batchPeriod).plusSeconds(10L);

        return batchLogRepository.findFirstByBatchNameAndStartTimeGreaterThanEqualOrderByUpdatedAtDesc(batchName, targetStartTime);
    }

    public Optional<BatchLog> findInProgressBatchInSameGroupExceptBatchLogId(String batchGroupName, Long batchLogId) {
        return batchLogRepository.findFirstByBatchGroupNameAndBatchStatusAndIdNot(batchGroupName, BatchStatus.IN_PROGRESS, batchLogId);
    }
}
