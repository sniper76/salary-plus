package ag.act.service.download.datamatrix.csv.generator;

import ag.act.enums.download.matrix.UserRetentionWeeklyCsvDataType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserRetentionWeeklyCsvGeneratorResolver {

    // default provider
    private final UserRetentionWeeklyCsvByWeekGenerator userRetentionWeeklyCsvRecordByWeekProvider;
    private final List<UserRetentionWeeklyCsvGenerator> userRetentionWeeklyCsvGenerators;

    public UserRetentionWeeklyCsvGenerator resolve(
        UserRetentionWeeklyCsvDataType userRetentionWeeklyCsvDataType
    ) {
        return userRetentionWeeklyCsvGenerators.stream()
            .filter(it -> it.supports(userRetentionWeeklyCsvDataType.getRowDataType()))
            .findFirst().orElse(userRetentionWeeklyCsvRecordByWeekProvider);
    }
}
