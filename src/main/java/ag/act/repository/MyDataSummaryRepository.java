package ag.act.repository;

import ag.act.entity.MyDataSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface MyDataSummaryRepository extends JpaRepository<MyDataSummary, Long> {
    Optional<MyDataSummary> findByUserId(Long userId);

    void deleteAllByUserId(Long userId);

    boolean existsByUserIdAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(Long userId, LocalDateTime startDateTime, LocalDateTime endDateTime);

}
