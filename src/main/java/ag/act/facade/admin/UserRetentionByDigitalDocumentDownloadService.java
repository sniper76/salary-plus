package ag.act.facade.admin;

import ag.act.dto.datamatrix.UserRetentionWeeklyCsvRequestDto;
import ag.act.dto.digitaldocument.DigitalDocumentProgressPeriodDto;
import ag.act.dto.download.DownloadFile;
import ag.act.enums.download.matrix.UserRetentionWeeklyCsvDataType;
import ag.act.service.digitaldocument.DigitalDocumentService;
import ag.act.service.digitaldocument.campaign.CampaignService;
import ag.act.service.download.datamatrix.UserRetentionWeeklyCsvDownloadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserRetentionByDigitalDocumentDownloadService {

    private final CampaignService campaignService;
    private final DigitalDocumentService digitalDocumentService;
    private final UserRetentionWeeklyCsvDownloadService userRetentionWeeklyCsvDownloadService;

    public DownloadFile downloadByCampaignId(UserRetentionWeeklyCsvDataType userRetentionWeeklyCsvDataType, Long campaignId) {
        final Long digitalDocumentPostId = getSourcePostId(campaignId);
        final DigitalDocumentProgressPeriodDto digitalDocumentDto = getDigitalDocumentPeriod(digitalDocumentPostId);

        final var userRetentionWeeklyCsvRequestDto = UserRetentionWeeklyCsvRequestDto.newRequestDtoOfCampaign(
            campaignId,
            digitalDocumentDto
        );

        return userRetentionWeeklyCsvDownloadService.createAndDownloadCsv(
            userRetentionWeeklyCsvDataType,
            userRetentionWeeklyCsvRequestDto
        );
    }

    public DownloadFile downloadByDigitalDocumentId(UserRetentionWeeklyCsvDataType userRetentionWeeklyCsvDataType, Long digitalDocumentId) {
        final Long digitalDocumentPostId = getDigitalDocumentPostId(digitalDocumentId);
        final DigitalDocumentProgressPeriodDto digitalDocumentDto = getDigitalDocumentPeriod(digitalDocumentPostId);

        final var userRetentionWeeklyCsvRequestDto = UserRetentionWeeklyCsvRequestDto.newRequestDtoOfDigitalDocument(
            digitalDocumentId,
            digitalDocumentDto
        );

        return userRetentionWeeklyCsvDownloadService.createAndDownloadCsv(
            userRetentionWeeklyCsvDataType,
            userRetentionWeeklyCsvRequestDto
        );
    }

    private DigitalDocumentProgressPeriodDto getDigitalDocumentPeriod(Long digitalDocumentId) {
        return digitalDocumentService.getDigitalDocumentProgressPeriod(digitalDocumentId);
    }

    private Long getSourcePostId(Long campaignId) {
        return campaignService.getSourcePostId(campaignId);
    }

    private Long getDigitalDocumentPostId(Long digitalDocumentId) {
        return digitalDocumentService.getDigitalDocumentPostId(digitalDocumentId);
    }
}
