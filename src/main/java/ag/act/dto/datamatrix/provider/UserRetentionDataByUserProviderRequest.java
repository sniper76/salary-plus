package ag.act.dto.datamatrix.provider;

import ag.act.dto.SimpleUserDto;
import ag.act.dto.datamatrix.UserRetentionDataMapPeriodDto;
import ag.act.enums.download.matrix.UserRetentionWeeklyCsvDataType;

public interface UserRetentionDataByUserProviderRequest {

    SimpleUserDto getSimpleUserDto();

    UserRetentionDataMapPeriodDto getUserRetentionDataMapPeriodDto();

    UserRetentionWeeklyCsvDataType getUserRetentionWeeklyCsvDataType();
}
