package ag.act.service.download.datamatrix.data.provider;

import ag.act.enums.download.matrix.UserRetentionWeeklyCsvDataType;
import ag.act.service.user.UserVerificationHistoryService;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.Map;

@RequiredArgsConstructor
public abstract class UserRetentionDataByWeekProvider<T> {
    protected final UserVerificationHistoryService userVerificationHistoryService;

    public abstract boolean supports(UserRetentionWeeklyCsvDataType userRetentionWeeklyCsvDataType);

    public abstract Map<LocalDate, Double> getRetentionDataMap(T userRetentionDataByWeekProviderRequest);
}
