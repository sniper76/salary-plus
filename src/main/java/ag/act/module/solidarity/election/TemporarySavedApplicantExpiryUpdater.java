package ag.act.module.solidarity.election;

import ag.act.entity.SolidarityLeaderApplicant;
import ag.act.entity.solidarity.election.SolidarityLeaderElection;
import ag.act.service.solidarity.election.SolidarityLeaderApplicantService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
@Transactional
public class TemporarySavedApplicantExpiryUpdater {

    private final SolidarityLeaderApplicantService solidarityLeaderApplicantService;

    public void expireIfApplicable(SolidarityLeaderElection leaderElection) {
        if (leaderElection.isPendingElection() || !leaderElection.isVotePeriod()) {
            return;
        }

        final List<SolidarityLeaderApplicant> temporarySavedApplicants = getTemporarySavedSolidarityLeaderApplicants(leaderElection);

        solidarityLeaderApplicantService.updateTemporarySavedApplicantsToExpired(temporarySavedApplicants);
    }

    private List<SolidarityLeaderApplicant> getTemporarySavedSolidarityLeaderApplicants(SolidarityLeaderElection leaderElection) {
        return solidarityLeaderApplicantService.getSavedSolidarityLeaderApplicantsByElectionId(leaderElection.getId());
    }
}
