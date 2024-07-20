package ag.act.dto.datamatrix;

import ag.act.dto.digitaldocument.DigitalDocumentProgressPeriodDto;
import lombok.Getter;

@Getter
public class UserRetentionWeeklyCsvRequestDto {

    private Long campaignId;
    private Long digitalDocumentId;
    private DigitalDocumentProgressPeriodDto digitalDocumentProgressPeriodDto;

    private UserRetentionWeeklyCsvRequestDto(
        Long campaignId,
        Long digitalDocumentId,
        DigitalDocumentProgressPeriodDto digitalDocumentProgressPeriodDto
    ) {
        this.campaignId = campaignId;
        this.digitalDocumentId = digitalDocumentId;
        this.digitalDocumentProgressPeriodDto = digitalDocumentProgressPeriodDto;
    }

    public static UserRetentionWeeklyCsvRequestDto newRequestDtoOfCampaign(
        Long campaignId,
        DigitalDocumentProgressPeriodDto digitalDocumentProgressPeriodDto
    ) {
        return new UserRetentionWeeklyCsvRequestDto(
            campaignId, null, digitalDocumentProgressPeriodDto
        );
    }

    public static UserRetentionWeeklyCsvRequestDto newRequestDtoOfDigitalDocument(
        Long digitalDocumentId,
        DigitalDocumentProgressPeriodDto digitalDocumentProgressPeriodDto
    ) {
        return new UserRetentionWeeklyCsvRequestDto(
            null, digitalDocumentId, digitalDocumentProgressPeriodDto
        );
    }
}
