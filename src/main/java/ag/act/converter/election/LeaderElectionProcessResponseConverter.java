package ag.act.converter.election;

import ag.act.converter.election.process.detail.LeaderElectionProcessDetailResponseConverterResolver;
import ag.act.converter.election.process.label.LeaderElectionProcessLabelResponseConverterResolver;
import ag.act.entity.solidarity.election.SolidarityLeaderElection;
import ag.act.enums.solidarity.election.SolidarityLeaderElectionStatus;
import ag.act.model.LeaderElectionProcessDetailResponse;
import ag.act.model.LeaderElectionProcessLabelResponse;
import ag.act.model.LeaderElectionProcessResponse;
import ag.act.module.solidarity.election.ISolidarityLeaderElection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Component
public class LeaderElectionProcessResponseConverter implements ISolidarityLeaderElection.ElectionDetailLabel {
    private final LeaderElectionProcessDetailResponseConverterResolver leaderElectionProcessDetailResponseConverterResolver;
    private final LeaderElectionProcessLabelResponseConverterResolver leaderElectionProcessLabelResponseConverterResolver;

    public List<LeaderElectionProcessResponse> convert(
        SolidarityLeaderElection solidarityLeaderElection
    ) {
        final List<SolidarityLeaderElectionStatus> leaderElectionStatuses = Arrays
            .stream(SolidarityLeaderElectionStatus.values())
            .filter(status -> !status.isPendingStatus())
            .toList();

        return leaderElectionStatuses.stream()
            .map(status -> toLeaderElectionProcessResponse(solidarityLeaderElection, status))
            .toList();
    }

    private LeaderElectionProcessResponse toLeaderElectionProcessResponse(
        SolidarityLeaderElection solidarityLeaderElection,
        SolidarityLeaderElectionStatus status
    ) {
        return new LeaderElectionProcessResponse()
            .title(status.getDisplayName())
            .electionStatus(status.name())
            .detail(getDetail(solidarityLeaderElection, status))
            .label(getLabel(solidarityLeaderElection, status));
    }

    private LeaderElectionProcessLabelResponse getLabel(
        SolidarityLeaderElection solidarityLeaderElection,
        SolidarityLeaderElectionStatus status
    ) {
        return leaderElectionProcessLabelResponseConverterResolver
            .resolve(status)
            .convert(solidarityLeaderElection);
    }

    private LeaderElectionProcessDetailResponse getDetail(
        SolidarityLeaderElection solidarityLeaderElection,
        SolidarityLeaderElectionStatus status
    ) {
        return leaderElectionProcessDetailResponseConverterResolver
            .resolve(status)
            .convert(solidarityLeaderElection);
    }
}
