package ag.act.service.download.datamatrix.data.provider;

import ag.act.enums.download.matrix.UserRetentionWeeklyCsvDataType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@SuppressWarnings("rawtypes")
@Component
@RequiredArgsConstructor
public class UserRetentionDataByWeekProviderResolver {
    private final List<UserRetentionDataByWeekProvider> userRetentionDataByWeekProviderList;
    private final PinVerificationGivenRegisterByWeekProvider pinVerificationGivenRegisterByWeekGenerator;

    public UserRetentionDataByWeekProvider resolve(UserRetentionWeeklyCsvDataType type) {
        return userRetentionDataByWeekProviderList.stream()
            .filter(generator -> generator.supports(type))
            .findFirst()
            .orElse(pinVerificationGivenRegisterByWeekGenerator);
    }
}
