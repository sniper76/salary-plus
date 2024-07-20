package ag.act.validator;

import ag.act.dto.GetPostDigitalDocumentSearchDto;
import ag.act.enums.DigitalDocumentType;
import ag.act.exception.BadRequestException;
import org.springframework.stereotype.Component;

@Component
public class GetDigitalDocumentOfAcceptorRequestValidator {

    public void validate(GetPostDigitalDocumentSearchDto searchDto) {
        if (searchDto.getDigitalDocumentType() != DigitalDocumentType.DIGITAL_PROXY) {
            throw new BadRequestException("전자문서 의결권위임만 조회가 가능합니다.");
        }
    }
}
