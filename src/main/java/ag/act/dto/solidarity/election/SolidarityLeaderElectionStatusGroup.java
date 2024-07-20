package ag.act.dto.solidarity.election;

import ag.act.enums.solidarity.election.SolidarityLeaderElectionStatus;
import ag.act.enums.solidarity.election.SolidarityLeaderElectionStatusDetails;

public record SolidarityLeaderElectionStatusGroup(
    SolidarityLeaderElectionStatus electionStatus,
    SolidarityLeaderElectionStatusDetails electionStatusDetails
) {

    public static final SolidarityLeaderElectionStatusGroup CANDIDATE_REGISTER_PENDING_STATUS_GROUP = new SolidarityLeaderElectionStatusGroup(
        SolidarityLeaderElectionStatus.CANDIDATE_REGISTRATION_PENDING_PERIOD,
        SolidarityLeaderElectionStatusDetails.IN_PROGRESS
    );

    public static final SolidarityLeaderElectionStatusGroup CANDIDATE_REGISTER_STATUS_GROUP = new SolidarityLeaderElectionStatusGroup(
        SolidarityLeaderElectionStatus.CANDIDATE_REGISTRATION_PERIOD,
        SolidarityLeaderElectionStatusDetails.IN_PROGRESS
    );

    public static final SolidarityLeaderElectionStatusGroup VOTE_STATUS_GROUP = new SolidarityLeaderElectionStatusGroup(
        SolidarityLeaderElectionStatus.VOTE_PERIOD,
        SolidarityLeaderElectionStatusDetails.IN_PROGRESS
    );

    public static final SolidarityLeaderElectionStatusGroup FINISHED_ON_TIME_STATUS_GROUP = new SolidarityLeaderElectionStatusGroup(
        SolidarityLeaderElectionStatus.FINISHED,
        SolidarityLeaderElectionStatusDetails.FINISHED_ON_TIME
    );

    public static final SolidarityLeaderElectionStatusGroup FINISHED_BY_ADMIN_STATUS_GROUP = new SolidarityLeaderElectionStatusGroup(
        SolidarityLeaderElectionStatus.FINISHED,
        SolidarityLeaderElectionStatusDetails.FINISHED_BY_ADMIN_SELECTION
    );

    public static final SolidarityLeaderElectionStatusGroup FINISHED_ON_TIME_WITH_NO_WINNER_STATUS_GROUP =
        new SolidarityLeaderElectionStatusGroup(
            SolidarityLeaderElectionStatus.FINISHED,
            SolidarityLeaderElectionStatusDetails.FINISHED_ON_TIME_WITH_NO_WINNER
        );

    public static final SolidarityLeaderElectionStatusGroup FINISHED_EARLY_STATUS_GROUP = new SolidarityLeaderElectionStatusGroup(
        SolidarityLeaderElectionStatus.FINISHED,
        SolidarityLeaderElectionStatusDetails.FINISHED_EARLY
    );

    public static final SolidarityLeaderElectionStatusGroup FINISHED_BY_NO_CANDIDATE_STATUS_GROUP = new SolidarityLeaderElectionStatusGroup(
        SolidarityLeaderElectionStatus.FINISHED,
        SolidarityLeaderElectionStatusDetails.FINISHED_BY_NO_CANDIDATE
    );
}
