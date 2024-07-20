package ag.act.converter.election.process.label;


import ag.act.enums.solidarity.election.SolidarityLeaderElectionStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class LeaderElectionProcessLabelResponseConverterResolver {

    private final List<LeaderElectionProcessLabelResponseConverter> converters;
    private final NullProcessLabelResponseConverter nullProcessLabelResponseConverter;

    public LeaderElectionProcessLabelResponseConverter resolve(SolidarityLeaderElectionStatus status) {
        return converters.stream()
            .filter(converter -> converter.supports(status))
            .findFirst()
            .orElse(nullProcessLabelResponseConverter);
    }
}
