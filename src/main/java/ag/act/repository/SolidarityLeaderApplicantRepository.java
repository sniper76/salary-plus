package ag.act.repository;

import ag.act.dto.SolidarityLeaderApplicantDto;
import ag.act.dto.SolidarityLeaderApplicationDto;
import ag.act.entity.SolidarityLeaderApplicant;
import ag.act.enums.solidarity.election.SolidarityLeaderElectionApplyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SolidarityLeaderApplicantRepository extends JpaRepository<SolidarityLeaderApplicant, Long> {
    boolean existsBySolidarityIdAndUserIdAndVersion(Long solidarityId, Long userId, int version);

    Optional<SolidarityLeaderApplicant> findBySolidarityIdAndUserIdAndVersion(Long solidarityId, Long userId, int version);

    @Query(value = """
        SELECT new ag.act.dto.SolidarityLeaderApplicationDto(
            u,
            s.code,
            s.standardCode,
            s.name,
            sla.solidarityLeaderElectionId,
            sla.id,
            sla.applyStatus,
            sla.reasonsForApplying,
            sla.knowledgeOfCompanyManagement,
            sla.goals,
            sla.commentsForStockHolder
        )
        FROM SolidarityLeaderApplicant sla
        JOIN User u ON u.id = sla.userId
        JOIN SolidarityLeaderElection sle ON sle.id = sla.solidarityLeaderElectionId
        JOIN Stock s ON s.code = sle.stockCode
        WHERE sle.id = :solidarityLeaderElectionId
        AND sla.id = :solidarityApplicationId
        AND sla.applyStatus IN :applyStatuses
        """)
    Optional<SolidarityLeaderApplicationDto> findBySolidarityApplicationIdAndStockCode(
        Long solidarityLeaderElectionId,
        Long solidarityApplicationId,
        List<SolidarityLeaderElectionApplyStatus> applyStatuses
    );

    Optional<SolidarityLeaderApplicant> findByIdAndSolidarityLeaderElectionId(Long id, Long solidarityLeaderElectionId);

    @Query("""
        SELECT new ag.act.dto.SolidarityLeaderApplicantDto(
                sla.id,
                u.id,
                u.nickname,
                u.profileImageUrl,
                uhs.quantity,
                sla.commentsForStockHolder
             )
          FROM SolidarityLeaderApplicant sla
         INNER JOIN SolidarityLeaderElection sle
            ON sla.solidarityLeaderElectionId = sle.id
           AND sle.id = :solidarityLeaderElectionId
         INNER JOIN User u
            ON sla.userId = u.id
         INNER JOIN Solidarity s
            ON s.id = sla.solidarityId
         INNER JOIN UserHoldingStock uhs
            ON uhs.userId = u.id
           AND uhs.stockCode = s.stockCode
         WHERE s.id = :solidarityId
           AND sla.applyStatus = ag.act.enums.solidarity.election.SolidarityLeaderElectionApplyStatus.COMPLETE
         ORDER BY sla.createdAt ASC
        """)
    List<SolidarityLeaderApplicantDto> findAllBySolidarityIdAndSolidarityLeaderElectionIdOrderByCreatedAtAsc(
        Long solidarityId,
        Long solidarityLeaderElectionId
    );

    @Query("""
        SELECT sla
          FROM SolidarityLeaderApplicant sla
         INNER JOIN SolidarityLeaderElection sle
            ON sla.solidarityLeaderElectionId = sle.id
           AND sle.electionStatus != ag.act.enums.solidarity.election.SolidarityLeaderElectionStatus.FINISHED
         INNER JOIN User u
            ON sla.userId = u.id
         WHERE u.id = :userId
           AND sla.applyStatus = ag.act.enums.solidarity.election.SolidarityLeaderElectionApplyStatus.COMPLETE
        """)
    List<SolidarityLeaderApplicant> findAllActiveAndCompletedSolidarityLeaderApplicantsByUserId(Long userId);

    @Query(value = """
        SELECT new ag.act.dto.SolidarityLeaderApplicationDto(
            u,
            st.code,
            st.standardCode,
            st.name,
            sla.solidarityLeaderElectionId,
            sla.id,
            sla.applyStatus,
            sla.reasonsForApplying,
            sla.knowledgeOfCompanyManagement,
            sla.goals,
            sla.commentsForStockHolder
        )
        FROM SolidarityLeaderApplicant sla
        INNER JOIN Solidarity s ON s.id = sla.solidarityId
        INNER JOIN User u ON u.id = sla.userId
        INNER JOIN Stock st ON st.code = s.stockCode
        WHERE s.stockCode = :stockCode
        AND sla.userId = :userId
        AND sla.version = :version
        AND sla.applyStatus IN :applyStatuses
        ORDER BY sla.createdAt DESC
        LIMIT 1
        """)
    Optional<SolidarityLeaderApplicationDto> findLatestSolidarityLeaderApplicantByStockCodeAndUserIdAndVersion(
        String stockCode,
        Long userId,
        int version,
        List<SolidarityLeaderElectionApplyStatus> applyStatuses
    );

    boolean existsBySolidarityLeaderElectionIdAndUserIdAndApplyStatusInAndVersion(
        Long solidarityLeaderElectionId,
        Long userId,
        List<SolidarityLeaderElectionApplyStatus> applyStatuses,
        int version
    );

    int countBySolidarityLeaderElectionIdAndApplyStatus(Long solidarityLeaderElectionId, SolidarityLeaderElectionApplyStatus applyStatus);

    List<SolidarityLeaderApplicant> findAllBySolidarityLeaderElectionIdAndApplyStatus(
        Long solidarityLeaderElectionId,
        SolidarityLeaderElectionApplyStatus solidarityLeaderElectionApplyStatus
    );
}
