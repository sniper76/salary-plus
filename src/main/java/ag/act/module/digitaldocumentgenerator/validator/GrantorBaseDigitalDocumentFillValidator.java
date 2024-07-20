package ag.act.module.digitaldocumentgenerator.validator;

import ag.act.entity.digitaldocument.DigitalDocumentUser;
import ag.act.exception.InternalServerException;
import org.apache.commons.lang3.StringUtils;

public abstract class GrantorBaseDigitalDocumentFillValidator extends BaseDigitalDocumentFillValidator {

    public void validateGrantor(DigitalDocumentUser grantor) {
        if (StringUtils.isBlank(grantor.getName())) {
            throw new InternalServerException("위임인의 이름이 존재하지 않습니다.");
        }
        if (grantor.getBirthDate() == null) {
            throw new InternalServerException("위임인의 생년월일이 존재하지 않습니다.");
        }
        if (grantor.getGender() == null) {
            throw new InternalServerException("위임인의 성별값이 존재하지 않습니다.");
        }
    }
}
