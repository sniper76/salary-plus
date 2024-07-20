package ag.act.module.solidarity.election;

import ag.act.entity.solidarity.election.SolidarityLeaderElection;
import ag.act.service.solidarity.election.SolidarityLeaderApplicantService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Transactional
@Component
public class CandidateCountModifier {

    private final SolidarityLeaderApplicantService solidarityLeaderApplicantService;

    public void setIfApplicable(SolidarityLeaderElection solidarityLeaderElection) {
        final int totalNumberOfApplicants = getTotalNumberOfApplicants(solidarityLeaderElection);
        solidarityLeaderElection.setCandidateCount(totalNumberOfApplicants);
    }

    private int getTotalNumberOfApplicants(SolidarityLeaderElection solidarityLeaderElection) {
        return solidarityLeaderApplicantService.countCompletedApplicantsByIdAndStatus(solidarityLeaderElection.getId());
    }
}
