package ag.act.dto.datamatrix.provider;

import ag.act.dto.SimpleUserDto;
import ag.act.dto.datamatrix.UserRetentionDataMapPeriodDto;
import ag.act.dto.digitaldocument.DigitalDocumentProgressPeriodDto;
import ag.act.enums.download.matrix.UserRetentionWeeklyCsvDataType;

import java.util.List;

public class UserRetentionDataProviderRequest
    implements UserRetentionDataOfDigitalDocumentByWeekProviderRequest,
    UserRetentionDataByWeekProviderRequest,
    UserRetentionDataByUserProviderRequest {

    private List<Long> digitalDocumentIds;
    private UserRetentionDataMapPeriodDto userRetentionDataMapPeriodDto;
    private SimpleUserDto simpleUserDto;
    private DigitalDocumentProgressPeriodDto digitalDocumentProgressPeriodDto;
    private UserRetentionWeeklyCsvDataType userRetentionWeeklyCsvDataType;

    public UserRetentionDataProviderRequest(
        List<Long> digitalDocumentIds,
        UserRetentionDataMapPeriodDto userRetentionDataMapPeriodDto,
        DigitalDocumentProgressPeriodDto digitalDocumentProgressPeriodDto
    ) {
        this.digitalDocumentIds = digitalDocumentIds;
        this.userRetentionDataMapPeriodDto = userRetentionDataMapPeriodDto;
        this.digitalDocumentProgressPeriodDto = digitalDocumentProgressPeriodDto;
    }

    public UserRetentionDataProviderRequest(UserRetentionDataMapPeriodDto userRetentionDataMapPeriodDto) {
        this.userRetentionDataMapPeriodDto = userRetentionDataMapPeriodDto;
    }

    public UserRetentionDataProviderRequest(
        UserRetentionDataMapPeriodDto userRetentionDataMapPeriodDto,
        SimpleUserDto simpleUserDto,
        UserRetentionWeeklyCsvDataType userRetentionWeeklyCsvDataType
    ) {
        this.userRetentionDataMapPeriodDto = userRetentionDataMapPeriodDto;
        this.simpleUserDto = simpleUserDto;
        this.userRetentionWeeklyCsvDataType = userRetentionWeeklyCsvDataType;
    }

    @Override
    public List<Long> getDigitalDocumentIds() {
        return digitalDocumentIds;
    }

    @Override
    public UserRetentionDataMapPeriodDto getUserRetentionDataMapPeriodDto() {
        return userRetentionDataMapPeriodDto;
    }

    @Override
    public UserRetentionWeeklyCsvDataType getUserRetentionWeeklyCsvDataType() {
        return userRetentionWeeklyCsvDataType;
    }

    @Override
    public SimpleUserDto getSimpleUserDto() {
        return simpleUserDto;
    }

    @Override
    public DigitalDocumentProgressPeriodDto getDigitalDocumentProgressPeriodDto() {
        return digitalDocumentProgressPeriodDto;
    }
}
