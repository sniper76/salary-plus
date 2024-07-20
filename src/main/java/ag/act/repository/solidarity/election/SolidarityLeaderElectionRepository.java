package ag.act.repository.solidarity.election;

import ag.act.entity.solidarity.election.SolidarityLeaderElection;
import ag.act.enums.solidarity.election.SolidarityLeaderElectionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SolidarityLeaderElectionRepository extends JpaRepository<SolidarityLeaderElection, Long> {

    Optional<SolidarityLeaderElection> findByStockCodeAndElectionStatusIn(
        String stockCode,
        List<SolidarityLeaderElectionStatus> solidarityLeaderElectionStatuses
    );

    @Query("""
        select e
        from SolidarityLeaderElection e
            where e.stockCode = :stockCode
            and (e.displayEndDateTime is null or e.displayEndDateTime >= current_timestamp )
            and e.electionStatus in :solidarityLeaderElectionStatuses
            order by e.createdAt desc
            limit 1
        """)
    Optional<SolidarityLeaderElection> findByStockCodeAndVoteClosingDateTimeAndElectionStatusIn(
        String stockCode,
        List<SolidarityLeaderElectionStatus> solidarityLeaderElectionStatuses
    );

    boolean existsByStockCodeAndElectionStatusIn(
        String stockCode,
        List<SolidarityLeaderElectionStatus> solidarityLeaderElectionStatus
    );

    Optional<SolidarityLeaderElection> findTopByStockCodeOrderByCreatedAtDesc(String stockCode);

    Optional<SolidarityLeaderElection> findByIdAndElectionStatusIn(
        Long id,
        List<SolidarityLeaderElectionStatus> solidarityLeaderElectionStatuses
    );

    Optional<SolidarityLeaderElection> findByPostId(Long postId);

    List<SolidarityLeaderElection> findAllByElectionStatusIn(List<SolidarityLeaderElectionStatus> solidarityLeaderElectionStatus);
}
