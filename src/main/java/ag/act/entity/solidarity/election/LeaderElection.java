package ag.act.entity.solidarity.election;

import ag.act.enums.solidarity.election.SolidarityLeaderElectionStatus;
import ag.act.enums.solidarity.election.SolidarityLeaderElectionStatusDetails;
import ag.act.exception.BadRequestException;

import java.time.LocalDateTime;

import static ag.act.enums.solidarity.election.SolidarityLeaderElectionStatusDetails.FINISHED_EARLY;
import static ag.act.enums.solidarity.election.SolidarityLeaderElectionStatusDetails.FINISHED_ON_TIME;

public interface LeaderElection {

    SolidarityLeaderElectionStatus getElectionStatus();

    SolidarityLeaderElectionStatusDetails getElectionStatusDetails();

    LocalDateTime getVoteStartDateTime();

    LocalDateTime getVoteEndDateTime();

    LocalDateTime getCandidateRegistrationStartDateTime();

    LocalDateTime getCandidateRegistrationEndDateTime();

    default boolean isActiveElection() {
        return getElectionStatus().isVotePeriodStatus()
            || getElectionStatus().isCandidateRegistrationPeriodStatus();
    }

    default boolean isFinishedElection() {
        return getElectionStatus().isFinishedStatus();
    }

    default boolean isFinishedByAdminElection() {
        return getElectionStatusDetails().isFinishedByAdminElection();
    }

    default boolean isFinishedEarlyElection() {
        return getElectionStatus().isFinishedStatus() && getElectionStatusDetails().isFinishedEarlyStatus();
    }

    default boolean isFinishedOnTimeElection() {
        return getElectionStatus().isFinishedStatus() && getElectionStatusDetails().isFinishedOnTimeStatus();
    }

    default boolean isFinishedOnTimeWithNoWinnerElection() {
        return getElectionStatus().isFinishedStatus() && getElectionStatusDetails().isFinishedOnTimeWithNoWinnerStatus();
    }

    default boolean isFinishedNoCandidateElection() {
        return getElectionStatus().isFinishedStatus() && getElectionStatusDetails().isFinishedNoCandidateStatus();
    }

    default boolean isPendingElection() {
        return getElectionStatus().isCandidateRegistrationPendingPeriodStatus();
    }

    default boolean isCandidateRegistrationPeriodElection() {
        return getElectionStatus().isCandidateRegistrationPeriodStatus();
    }

    default boolean isVotePeriodElection() {
        return getElectionStatus().isVotePeriodStatus();
    }

    default boolean isVotePeriod() {
        if (isPendingElection()) {
            return false;
        }

        final LocalDateTime currentDateTime = getTodayLocalDateTime();

        return isVotePeriodStarted(currentDateTime)
            && !isVotePeriodEnded(currentDateTime);
    }

    private boolean isVotePeriodEnded(LocalDateTime currentDateTime) {
        return getVoteEndDateTime().isBefore(currentDateTime);
    }

    default boolean isVotePeriodEnded() {
        if (isPendingElection()) {
            return false;
        }
        return isVotePeriodEnded(getTodayLocalDateTime());
    }

    private boolean isVotePeriodStarted(LocalDateTime currentDateTime) {
        return getVoteStartDateTime().isBefore(currentDateTime)
            || getVoteStartDateTime().isEqual(currentDateTime);
    }

    default boolean isCandidateRegistrationPeriod() {
        if (isPendingElection()) {
            return false;
        }

        final LocalDateTime currentDateTime = getTodayLocalDateTime();

        return isCandidateRegistrationPeriodStarted(currentDateTime)
            && !isCandidateRegistrationPeriodEnded(currentDateTime);
    }

    default boolean isFromVoteToFinish() {
        if (isPendingElection()) {
            return false;
        }

        return isVotePeriodEnded()
            && isVotePeriodElection();
    }

    default boolean isFromCandidateRegistrationToVotePeriod() {
        if (isPendingElection()) {
            return false;
        }

        return isVotePeriod()
            && isCandidateRegistrationPeriodElection();
    }

    default boolean isFinishedWithResult() {
        return isFinishedOnTimeElection()
            || isFinishedEarlyElection()
            || isFinishedOnTimeWithNoWinnerElection();
    }

    default String getElectionFinishedLabel() {
        if (!isFinishedElection()) {
            throw new BadRequestException("주주대표 선출 투표가 종료되지 않았습니다.");
        }

        return isFinishedEarlyElection()
            ? FINISHED_EARLY.getDisplayName()
            : FINISHED_ON_TIME.getDisplayName();
    }

    private LocalDateTime getTodayLocalDateTime() {
        return LeaderElectionCurrentDateTimeProvider.get();
    }

    default boolean isCandidateRegistrationPeriodEnded() {
        return isCandidateRegistrationPeriodEnded(getTodayLocalDateTime());
    }

    private boolean isCandidateRegistrationPeriodEnded(LocalDateTime currentDateTime) {
        return getCandidateRegistrationEndDateTime().isBefore(currentDateTime);
    }

    private boolean isCandidateRegistrationPeriodStarted(LocalDateTime currentDateTime) {
        return getCandidateRegistrationStartDateTime().isBefore(currentDateTime)
            || getCandidateRegistrationStartDateTime().isEqual(currentDateTime);
    }
}
