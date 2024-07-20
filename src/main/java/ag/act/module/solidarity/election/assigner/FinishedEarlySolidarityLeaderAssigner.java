package ag.act.module.solidarity.election.assigner;

import ag.act.dto.election.ApplicantPollAnswerData;
import ag.act.dto.solidarity.election.SolidarityLeaderElectionStatusGroup;
import ag.act.entity.solidarity.election.SolidarityLeaderElection;
import ag.act.module.solidarity.election.SolidarityLeaderElectionPushRegister;
import ag.act.module.solidarity.election.SolidarityLeaderElectionSlackNotifier;
import ag.act.service.solidarity.election.SolidarityLeaderApplicantService;
import ag.act.service.solidarity.election.SolidarityLeaderService;
import ag.act.util.DateTimeUtil;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Transactional
@Component
public class FinishedEarlySolidarityLeaderAssigner extends AbstractLeaderAssigner {
    private final SolidarityLeaderElectionDecisionMaker solidarityLeaderElectionDecisionMaker;

    public FinishedEarlySolidarityLeaderAssigner(
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

        final Optional<ApplicantPollAnswerData> earlyFinishedWinner = findEarlyWinner(solidarityLeaderElection);

        if (earlyFinishedWinner.isEmpty()) {
            return;
        }

        final long winnerApplicantId = earlyFinishedWinner.get().applicantId();
        solidarityLeaderElection.setWinnerApplicantId(winnerApplicantId);
        solidarityLeaderElection.setElectionStatusGroup(SolidarityLeaderElectionStatusGroup.FINISHED_EARLY_STATUS_GROUP);
        solidarityLeaderElection.setVoteClosingDateTime(DateTimeUtil.getTodayLocalDateTime());

        notifySlackIfApplicable(solidarityLeaderElection);
        registerPushIfApplicable(solidarityLeaderElection);
        createWinnerSolidarityLeader(winnerApplicantId);
    }

    private Optional<ApplicantPollAnswerData> findEarlyWinner(SolidarityLeaderElection solidarityLeaderElection) {
        return solidarityLeaderElectionDecisionMaker.findEarlyWinner(solidarityLeaderElection);
    }

    private boolean isActiveVotePeriod(SolidarityLeaderElection solidarityLeaderElection) {
        return !solidarityLeaderElection.isFinishedElection()
            && solidarityLeaderElection.isActiveElection()
            && solidarityLeaderElection.isVotePeriod();
    }
}
