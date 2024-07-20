package ag.act.service.download.datamatrix.data.provider;

import ag.act.enums.download.matrix.UserRetentionWeeklyCsvDataType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@SuppressWarnings("rawtypes")
@Component
@RequiredArgsConstructor
public class UserRetentionDataByUserProviderResolver {
    private final List<UserRetentionDataByUserProvider> userRetentionDataByUserProviders;
    private final AllUsersGivenAllConditionsByWeekProvider allUsersGivenAllConditionsByWeekProvider;

    public UserRetentionDataByUserProvider resolve(UserRetentionWeeklyCsvDataType type) {
        return userRetentionDataByUserProviders.stream()
            .filter(generator -> generator.supports(type))
            .findFirst()
            .orElse(allUsersGivenAllConditionsByWeekProvider);
    }
}