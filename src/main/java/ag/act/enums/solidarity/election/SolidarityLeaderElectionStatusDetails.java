package ag.act.enums.solidarity.election;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SolidarityLeaderElectionStatusDetails {
    IN_PROGRESS("진행중"),
    FINISHED_ON_TIME("기간마감"),
    FINISHED_EARLY("조기마감"),
    FINISHED_BY_NO_CANDIDATE("후보자없음"),
    FINISHED_ON_TIME_WITH_NO_WINNER("선출된 주주대표 없음"),
    FINISHED_BY_ADMIN_SELECTION("관리자에 의해 마감"),
    ;

    private final String displayName;

    public static SolidarityLeaderElectionStatusDetails fromValue(String typeName) {
        try {
            return SolidarityLeaderElectionStatusDetails.valueOf(typeName.toUpperCase());
        } catch (Exception e) {
            return IN_PROGRESS;
        }
    }

    public boolean isFinishedEarlyStatus() {
        return this == FINISHED_EARLY;
    }

    public boolean isFinishedOnTimeStatus() {
        return this == FINISHED_ON_TIME;
    }

    public boolean isFinishedNoCandidateStatus() {
        return this == FINISHED_BY_NO_CANDIDATE;
    }

    public boolean isFinishedOnTimeWithNoWinnerStatus() {
        return this == FINISHED_ON_TIME_WITH_NO_WINNER;
    }

    public boolean isFinishedByAdminElection() {
        return this == FINISHED_BY_ADMIN_SELECTION;
    }
}
