package ag.act.module.solidarity.election.assigner;

import ag.act.dto.election.ApplicantPollAnswerData;
import ag.act.dto.solidarity.election.SolidarityLeaderElectionStatusGroup;
import ag.act.entity.solidarity.election.SolidarityLeaderElection;
import ag.act.module.solidarity.election.SolidarityLeaderElectionPushRegister;
import ag.act.module.solidarity.election.SolidarityLeaderElectionSlackNotifier;
import ag.act.service.solidarity.election.SolidarityLeaderApplicantService;
import ag.act.service.solidarity.election.SolidarityLeaderService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Transactional
@Component
public class FinishedOnTimeSolidarityLeaderAssigner extends AbstractLeaderAssigner {
    private final SolidarityLeaderElectionDecisionMaker solidarityLeaderElectionDecisionMaker;

    public FinishedOnTimeSolidarityLeaderAssigner(
        SolidarityLeaderService solidarityLeaderService,
        SolidarityLeaderApplicantService solidarityLeaderApplicantService,
        SolidarityLeaderElectionPushRegister solidarityLeaderElectionPushRegister,
        SolidarityLeaderElectionSlackNotifier solidarityLeaderElectionSlackNotifier,
        SolidarityLeaderElectionDecisionMaker solidarityLeaderElectionDecisionMaker
    ) {
        super(
            solidarityLeaderService,
            solidarityLeaderApplicantService,
            solidarityLeaderElectionPushRegister,
            solidarityLeaderElectionSlackNotifier
        );
        this.solidarityLeaderElectionDecisionMaker = solidarityLeaderElectionDecisionMaker;
    }

    public void assignLeader(SolidarityLeaderElection solidarityLeaderElection) {
        if (!isActiveVotePeriod(solidarityLeaderElection)) {
            return;
        }

        final Optional<ApplicantPollAnswerData> winner = findWinner(solidarityLeaderElection);

        if (winner.isPresent()) {
            final long winnerApplicantId = winner.get().applicantId();
            solidarityLeaderElection.setWinnerApplicantId(winnerApplicantId);
            createWinnerSolidarityLeader(winnerApplicantId);
            solidarityLeaderElection.setElectionStatusGroup(SolidarityLeaderElectionStatusGroup.FINISHED_ON_TIME_STATUS_GROUP);
        } else {
            solidarityLeaderElection.setElectionStatusGroup(SolidarityLeaderElectionStatusGroup.FINISHED_ON_TIME_WITH_NO_WINNER_STATUS_GROUP);
        }

        solidarityLeaderElection.setVoteClosingDateTime(solidarityLeaderElection.getVoteEndDateTime());
        notifySlackIfApplicable(solidarityLeaderElection);
        registerPushIfApplicable(solidarityLeaderElection);
    }

    private Optional<ApplicantPollAnswerData> findWinner(SolidarityLeaderElection solidarityLeaderElection) {
        return solidarityLeaderElectionDecisionMaker.findWinner(solidarityLeaderElection);
    }

    private boolean isActiveVotePeriod(SolidarityLeaderElection solidarityLeaderElection) {
        return !solidarityLeaderElection.isFinishedElection()
            && solidarityLeaderElection.isFromVoteToFinish();
    }
}
