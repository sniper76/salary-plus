package ag.act.dto.datamatrix;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class UserRetentionWeeklyCsvOfDigitalDocumentRequestDto {

    private final Long campaignId;
    private final Long digitalDocumentId;
    private final LocalDate digitalDocumentTargetStartDate;
    private final LocalDate digitalDocumentTargetEndDate;
    private final LocalDate today;

    private UserRetentionWeeklyCsvOfDigitalDocumentRequestDto(
        Long campaignId,
        Long digitalDocumentId,
        LocalDate digitalDocumentTargetStartDate,
        LocalDate digitalDocumentTargetEndDate,
        LocalDate today
    ) {
        this.campaignId = campaignId;
        this.digitalDocumentId = digitalDocumentId;
        this.digitalDocumentTargetStartDate = digitalDocumentTargetStartDate;
        this.digitalDocumentTargetEndDate = digitalDocumentTargetEndDate;
        this.today = today;
    }

    public static UserRetentionWeeklyCsvOfDigitalDocumentRequestDto newRequestDtoOfCampaign(
        Long campaignId,
        LocalDate digitalDocumentTargetStartDate,
        LocalDate digitalDocumentTargetEndDate,
        LocalDate today
    ) {
        return new UserRetentionWeeklyCsvOfDigitalDocumentRequestDto(
            campaignId, null, digitalDocumentTargetStartDate, digitalDocumentTargetEndDate, today
        );
    }

    public static UserRetentionWeeklyCsvOfDigitalDocumentRequestDto newRequestDtoOfDigitalDocument(
        Long digitalDocumentId,
        LocalDate digitalDocumentTargetStartDate,
        LocalDate digitalDocumentTargetEndDate,
        LocalDate today
    ) {
        return new UserRetentionWeeklyCsvOfDigitalDocumentRequestDto(
            null, digitalDocumentId, digitalDocumentTargetStartDate, digitalDocumentTargetEndDate, today
        );
    }
}
