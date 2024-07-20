package ag.act.repository;

import ag.act.entity.BatchLog;
import ag.act.enums.BatchStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BatchLogRepository extends JpaRepository<BatchLog, Long> {
    Optional<BatchLog> findFirstByBatchNameAndStartTimeGreaterThanEqualOrderByUpdatedAtDesc(String fileName, LocalDateTime startTime);

    Optional<BatchLog> findFirstByBatchNameOrderByUpdatedAtDesc(String fileName);

    Optional<BatchLog> findFirstByBatchGroupNameAndBatchStatusAndIdNot(String batchGroupName, BatchStatus batchStatus, Long batchLogId);

    List<BatchLog> findAllByBatchStatusAndStartTimeIsBefore(BatchStatus batchStatus, LocalDateTime minusMinutes);
}
