package ag.act.service.solidarity.election;

import ag.act.dto.solidarity.SolidarityLeaderElectionPeriod;
import ag.act.dto.solidarity.election.SolidarityLeaderElectionStatusGroup;
import ag.act.entity.solidarity.election.SolidarityLeaderElection;
import ag.act.repository.solidarity.election.SolidarityLeaderElectionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Transactional
public class SolidarityLeaderElectionCreateService {

    private final SolidarityLeaderElectionRepository solidarityLeaderElectionRepository;
    private final SolidarityLeaderElectionPeriodGenerator solidarityLeaderElectionPeriodGenerator;

    public SolidarityLeaderElection createPendingSolidarityLeaderElection(String stockCode) {
        SolidarityLeaderElection solidarityLeaderElection = new SolidarityLeaderElection();
        solidarityLeaderElection.setStockCode(stockCode);
        solidarityLeaderElection.setElectionStatusGroup(SolidarityLeaderElectionStatusGroup.CANDIDATE_REGISTER_PENDING_STATUS_GROUP);
        solidarityLeaderElection.setCandidateCount(0);

        return solidarityLeaderElectionRepository.save(solidarityLeaderElection);
    }

    public SolidarityLeaderElection startSolidarityLeaderElection(SolidarityLeaderElection solidarityLeaderElection) {

        final SolidarityLeaderElectionPeriod electionPeriod = solidarityLeaderElectionPeriodGenerator.generate();

        solidarityLeaderElection.setElectionStatusGroup(SolidarityLeaderElectionStatusGroup.CANDIDATE_REGISTER_STATUS_GROUP);
        solidarityLeaderElection.setCandidateRegistrationStartDateTime(electionPeriod.candidateRegistrationStartDateTime());
        solidarityLeaderElection.setCandidateRegistrationEndDateTime(electionPeriod.candidateRegistrationEndDateTime());
        solidarityLeaderElection.setVoteStartDateTime(electionPeriod.voteStartDateTime());
        solidarityLeaderElection.setVoteEndDateTime(electionPeriod.voteEndDateTime());

        return solidarityLeaderElectionRepository.save(solidarityLeaderElection);
    }
}
