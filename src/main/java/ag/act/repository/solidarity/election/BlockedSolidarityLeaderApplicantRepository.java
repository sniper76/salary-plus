package ag.act.repository.solidarity.election;

import ag.act.entity.solidarity.election.BlockedSolidarityLeaderApplicant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BlockedSolidarityLeaderApplicantRepository extends JpaRepository<BlockedSolidarityLeaderApplicant, Long> {
    Optional<BlockedSolidarityLeaderApplicant> findByStockCodeAndUserId(String stockCode, Long userId);
}
