package ag.act.service.download.datamatrix.data.provider;

import ag.act.enums.download.matrix.UserRetentionWeeklyCsvDataType;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.Map;

@RequiredArgsConstructor
public abstract class UserRetentionDataByUserProvider<T> {

    public abstract boolean supports(UserRetentionWeeklyCsvDataType userRetentionWeeklyCsvDataType);

    public abstract Map<LocalDate, String> getRetentionDataMap(T userRetentionDataByWeekProviderRequest);
}
