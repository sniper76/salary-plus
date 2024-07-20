package ag.act.module.digitaldocumentgenerator.validator;

import ag.act.entity.digitaldocument.IJointOwnershipDocument;
import ag.act.exception.InternalServerException;
import ag.act.module.digitaldocumentgenerator.dto.IGenerateHtmlDocumentDto;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class JointOwnershipDocumentFillValidator extends GrantorBaseDigitalDocumentFillValidator {
    private final DigitalDocumentAcceptorFillValidator digitalDocumentAcceptorFillValidator;

    public JointOwnershipDocumentFillValidator(DigitalDocumentAcceptorFillValidator digitalDocumentAcceptorFillValidator) {
        this.digitalDocumentAcceptorFillValidator = digitalDocumentAcceptorFillValidator;
    }

    @Override
    protected void validateTypeSpecific(IGenerateHtmlDocumentDto dto) {
        final IJointOwnershipDocument jointOwnershipDocument = (IJointOwnershipDocument) dto.getDigitalDocument();

        if (StringUtils.isBlank(jointOwnershipDocument.getCompanyRegistrationNumber())) {
            throw new InternalServerException("법인등록번호가 존재하지 않습니다.");
        }

        if (StringUtils.isBlank(jointOwnershipDocument.getContent())) {
            throw new InternalServerException("공동보유 내용이 존재하지 않습니다.");
        }

        digitalDocumentAcceptorFillValidator.validate(jointOwnershipDocument.getAcceptUserId());
    }
}
