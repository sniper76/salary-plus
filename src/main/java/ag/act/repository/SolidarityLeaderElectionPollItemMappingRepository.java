package ag.act.repository;

import ag.act.entity.solidarity.election.SolidarityLeaderElectionPollItemMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolidarityLeaderElectionPollItemMappingRepository extends JpaRepository<SolidarityLeaderElectionPollItemMapping, Long> {
    List<SolidarityLeaderElectionPollItemMapping> findAllBySolidarityLeaderElectionId(Long solidarityLeaderElectionId);

    List<SolidarityLeaderElectionPollItemMapping> findAllBySolidarityLeaderElectionIdAndPollItemIdIn(
        Long solidarityLeaderElectionId,
        List<Long> pollItemIds
    );
}
