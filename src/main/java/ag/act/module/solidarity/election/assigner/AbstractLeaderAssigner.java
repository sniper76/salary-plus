package ag.act.module.solidarity.election.assigner;

import ag.act.entity.SolidarityLeaderApplicant;
import ag.act.entity.solidarity.election.SolidarityLeaderElection;
import ag.act.module.solidarity.election.SolidarityLeaderElectionPushRegister;
import ag.act.module.solidarity.election.SolidarityLeaderElectionSlackNotifier;
import ag.act.service.solidarity.election.SolidarityLeaderApplicantService;
import ag.act.service.solidarity.election.SolidarityLeaderService;

public abstract class AbstractLeaderAssigner {

    private final SolidarityLeaderService solidarityLeaderService;
    private final SolidarityLeaderApplicantService solidarityLeaderApplicantService;
    private final SolidarityLeaderElectionPushRegister solidarityLeaderElectionPushRegister;
    private final SolidarityLeaderElectionSlackNotifier solidarityLeaderElectionSlackNotifier;

    protected AbstractLeaderAssigner(
        SolidarityLeaderService solidarityLeaderService,
        SolidarityLeaderApplicantService solidarityLeaderApplicantService,
        SolidarityLeaderElectionPushRegister solidarityLeaderElectionPushRegister,
        SolidarityLeaderElectionSlackNotifier solidarityLeaderElectionSlackNotifier
    ) {
        this.solidarityLeaderService = solidarityLeaderService;
        this.solidarityLeaderApplicantService = solidarityLeaderApplicantService;
        this.solidarityLeaderElectionPushRegister = solidarityLeaderElectionPushRegister;
        this.solidarityLeaderElectionSlackNotifier = solidarityLeaderElectionSlackNotifier;
    }

    protected void createWinnerSolidarityLeader(long winnerApplicantId) {
        final SolidarityLeaderApplicant winnerSolidarityLeaderApplicant = getSolidarityLeaderApplicant(winnerApplicantId);
        solidarityLeaderService.createSolidarityLeader(winnerSolidarityLeaderApplicant);
    }

    private SolidarityLeaderApplicant getSolidarityLeaderApplicant(long applicantId) {
        return solidarityLeaderApplicantService.getSolidarityLeaderApplicant(applicantId);
    }

    protected void registerPushIfApplicable(SolidarityLeaderElection solidarityLeaderElection) {
        solidarityLeaderElectionPushRegister.register(solidarityLeaderElection);
    }

    protected void notifySlackIfApplicable(SolidarityLeaderElection solidarityLeaderElection) {
        solidarityLeaderElectionSlackNotifier.notifyIfApplicable(solidarityLeaderElection);
    }
}
