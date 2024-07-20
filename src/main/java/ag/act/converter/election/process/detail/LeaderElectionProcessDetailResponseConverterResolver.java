package ag.act.converter.election.process.detail;

import ag.act.enums.solidarity.election.SolidarityLeaderElectionStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class LeaderElectionProcessDetailResponseConverterResolver {

    private final List<LeaderElectionProcessDetailResponseConverter> converters;
    private final NullLeaderProcessDetailResponseConverter nullLeaderProcessDetailResponseConverter;

    public LeaderElectionProcessDetailResponseConverter resolve(SolidarityLeaderElectionStatus status) {
        return converters.stream()
            .filter(converter -> converter.supports(status))
            .findFirst()
            .orElse(nullLeaderProcessDetailResponseConverter);
    }
}
