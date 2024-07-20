package ag.act.converter.election.process.detail;

import ag.act.converter.DateTimeConverter;
import ag.act.dto.election.LeaderElectionProcessDetailData;
import ag.act.entity.solidarity.election.SolidarityLeaderElection;
import ag.act.enums.solidarity.election.SolidarityLeaderElectionStatus;
import ag.act.model.LeaderElectionProcessDetailResponse;
import ag.act.module.solidarity.election.ISolidarityLeaderElection;

import java.time.LocalDateTime;

public abstract class LeaderElectionProcessDetailResponseConverter implements ISolidarityLeaderElection.ElectionDetailLabel {
    abstract boolean supports(SolidarityLeaderElectionStatus status);

    public abstract LeaderElectionProcessDetailResponse convert(SolidarityLeaderElection leaderElection);

    protected LeaderElectionProcessDetailResponse toResponse(LeaderElectionProcessDetailData leaderElectionProcessDetailData) {
        return new LeaderElectionProcessDetailResponse()
            .title(leaderElectionProcessDetailData.title())
            .value(leaderElectionProcessDetailData.value())
            .startDate(DateTimeConverter.convert(leaderElectionProcessDetailData.startDate()))
            .endDate(DateTimeConverter.convert(getEndOfPreviousDateTime(leaderElectionProcessDetailData.endDate())))
            .unit(leaderElectionProcessDetailData.unit());
    }

    private LocalDateTime getEndOfPreviousDateTime(LocalDateTime dateTime) {
        return dateTime.minusSeconds(1);
    }
}
