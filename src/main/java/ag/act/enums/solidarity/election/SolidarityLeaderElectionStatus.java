package ag.act.enums.solidarity.election;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@Getter
@RequiredArgsConstructor
public enum SolidarityLeaderElectionStatus {
    CANDIDATE_REGISTRATION_PENDING_PERIOD("후보자 등록 대기", 0),
    CANDIDATE_REGISTRATION_PERIOD("후보자 등록 기간", 1),
    VOTE_PERIOD("주주대표 투표", 2),
    FINISHED("투표마감", 3),
    ;

    private final String displayName;
    private final int order;

    public static Optional<SolidarityLeaderElectionStatus> fromValue(String typeName) {
        try {
            return Optional.of(SolidarityLeaderElectionStatus.valueOf(typeName.toUpperCase()));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public static List<SolidarityLeaderElectionStatus> getActiveStatus() {
        return List.of(CANDIDATE_REGISTRATION_PENDING_PERIOD, CANDIDATE_REGISTRATION_PERIOD, VOTE_PERIOD);
    }

    public static List<SolidarityLeaderElectionStatus> getPreVoteStatus() {
        return List.of(CANDIDATE_REGISTRATION_PENDING_PERIOD, CANDIDATE_REGISTRATION_PERIOD);
    }

    public static List<SolidarityLeaderElectionStatus> getOngoingStatus() {
        return List.of(CANDIDATE_REGISTRATION_PERIOD, VOTE_PERIOD);
    }

    public boolean isVotePeriodStatus() {
        return this == VOTE_PERIOD;
    }

    public boolean isPendingStatus() {
        return this == CANDIDATE_REGISTRATION_PENDING_PERIOD;
    }

    public boolean isFinishedStatus() {
        return this == FINISHED;
    }

    public boolean isCandidateRegistrationPeriodStatus() {
        return this == CANDIDATE_REGISTRATION_PERIOD;
    }

    public boolean isCandidateRegistrationPendingPeriodStatus() {
        return this == CANDIDATE_REGISTRATION_PENDING_PERIOD;
    }
}
