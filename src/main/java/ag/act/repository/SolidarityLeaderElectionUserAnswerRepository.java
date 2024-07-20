package ag.act.repository;

import ag.act.entity.SolidarityLeaderElectionUserAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SolidarityLeaderElectionUserAnswerRepository extends JpaRepository<SolidarityLeaderElectionUserAnswer, Long> {

}
