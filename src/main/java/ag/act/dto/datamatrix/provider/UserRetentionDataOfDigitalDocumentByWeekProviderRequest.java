package ag.act.dto.datamatrix.provider;

import ag.act.dto.datamatrix.UserRetentionDataMapPeriodDto;
import ag.act.dto.digitaldocument.DigitalDocumentProgressPeriodDto;

import java.util.List;

public interface UserRetentionDataOfDigitalDocumentByWeekProviderRequest {

    List<Long> getDigitalDocumentIds();

    UserRetentionDataMapPeriodDto getUserRetentionDataMapPeriodDto();

    DigitalDocumentProgressPeriodDto getDigitalDocumentProgressPeriodDto();
}
