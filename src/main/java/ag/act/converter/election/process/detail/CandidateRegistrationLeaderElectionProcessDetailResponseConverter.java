package ag.act.converter.election.process.detail;

import ag.act.dto.election.LeaderElectionProcessDetailData;
import ag.act.entity.solidarity.election.SolidarityLeaderElection;
import ag.act.enums.solidarity.election.SolidarityLeaderElectionStatus;
import ag.act.model.LeaderElectionProcessDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Component
public class CandidateRegistrationLeaderElectionProcessDetailResponseConverter extends LeaderElectionProcessDetailResponseConverter {

    @Override
    public boolean supports(SolidarityLeaderElectionStatus status) {
        return status.isCandidateRegistrationPeriodStatus();
    }

    @Override
    public LeaderElectionProcessDetailResponse convert(SolidarityLeaderElection leaderElection) {
        final LocalDateTime startDateTime = leaderElection.getCandidateRegistrationStartDateTime();
        final LocalDateTime endDateTime = leaderElection.getCandidateRegistrationEndDateTime();

        return toResponse(
            new LeaderElectionProcessDetailData(
                APPLICANT_TITLE,
                (long) leaderElection.getCandidateCount(),
                startDateTime,
                endDateTime,
                UNIT
            )
        );
    }
}
