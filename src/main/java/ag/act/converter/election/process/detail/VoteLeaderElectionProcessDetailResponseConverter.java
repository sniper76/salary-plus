package ag.act.converter.election.process.detail;

import ag.act.dto.election.LeaderElectionProcessDetailData;
import ag.act.entity.solidarity.election.SolidarityLeaderElection;
import ag.act.enums.solidarity.election.SolidarityLeaderElectionStatus;
import ag.act.model.LeaderElectionProcessDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class VoteLeaderElectionProcessDetailResponseConverter extends LeaderElectionProcessDetailResponseConverter {

    @Override
    public boolean supports(SolidarityLeaderElectionStatus status) {
        return status.isVotePeriodStatus();
    }

    @Override
    public LeaderElectionProcessDetailResponse convert(SolidarityLeaderElection leaderElection) {
        final LocalDateTime startDateTime = leaderElection.getVoteStartDateTime();
        final LocalDateTime endDateTime = getEndDateTime(leaderElection);

        return toResponse(
            LeaderElectionProcessDetailData.withDateTimes(startDateTime, endDateTime)
        );
    }

    private LocalDateTime getEndDateTime(SolidarityLeaderElection leaderElection) {
        return Optional
            .ofNullable(leaderElection.getVoteClosingDateTime())
            .orElse(leaderElection.getVoteEndDateTime());
    }
}
