package ag.act.dto;

import ag.act.enums.DigitalAnswerType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DigitalDocumentUserAnswerDto {
    private Long digitalDocumentId;
    private Long digitalDocumentItemId;
    private Long digitalDocumentItemAnswerId;
    private Long userId;
    private DigitalAnswerType defaultAnswerType;
    private DigitalAnswerType userAnswerType;

    public DigitalDocumentUserAnswerDto(
        Long digitalDocumentId,
        Long digitalDocumentItemId,
        Long digitalDocumentItemAnswerId,
        Long userId,
        DigitalAnswerType defaultAnswerType,
        DigitalAnswerType userAnswerType
    ) {
        this.digitalDocumentId = digitalDocumentId;
        this.digitalDocumentItemId = digitalDocumentItemId;
        this.digitalDocumentItemAnswerId = digitalDocumentItemAnswerId;
        this.userId = userId;
        this.defaultAnswerType = defaultAnswerType;
        this.userAnswerType = userAnswerType;
    }
}
