package ag.act.module.digitaldocumentgenerator.validator;

import ag.act.entity.digitaldocument.IOtherDocument;
import ag.act.exception.InternalServerException;
import ag.act.module.digitaldocumentgenerator.dto.IGenerateHtmlDocumentDto;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class OtherDocumentFillValidator extends GrantorBaseDigitalDocumentFillValidator {

    @Override
    protected void validateTypeSpecific(IGenerateHtmlDocumentDto dto) {
        final IOtherDocument otherDocument = (IOtherDocument) dto.getDigitalDocument();
        if (StringUtils.isBlank(otherDocument.getTitle())) {
            throw new InternalServerException("전자문서 타이틀이 존재하지 않습니다.");
        }
        if (StringUtils.isBlank(otherDocument.getContent())) {
            throw new InternalServerException("전자문서 내용이 존재하지 않습니다.");
        }
    }
}
