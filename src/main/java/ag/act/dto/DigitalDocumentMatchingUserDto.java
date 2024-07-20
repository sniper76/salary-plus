package ag.act.dto;

import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.entity.digitaldocument.DigitalDocumentUser;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DigitalDocumentMatchingUserDto {
    private DigitalDocument digitalDocument;
    private DigitalDocumentUser digitalDocumentUser;

    public DigitalDocumentMatchingUserDto(DigitalDocument digitalDocument, DigitalDocumentUser digitalDocumentUser) {
        this.digitalDocument = digitalDocument;
        this.digitalDocumentUser = digitalDocumentUser;
    }
}
