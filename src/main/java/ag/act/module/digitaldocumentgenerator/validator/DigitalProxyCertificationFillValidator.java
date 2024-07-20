package ag.act.module.digitaldocumentgenerator.validator;

import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.entity.digitaldocument.DigitalDocumentUser;
import ag.act.entity.digitaldocument.IDigitalDocument;
import ag.act.entity.digitaldocument.IDigitalProxy;
import ag.act.exception.InternalServerException;
import ag.act.module.digitaldocumentgenerator.dto.GenerateHtmlCertificationDto;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@SuppressWarnings("DuplicatedCode")
@Component
public class DigitalProxyCertificationFillValidator implements IDigitalDocumentCertificationFillValidator {

    @Override
    public void validate(GenerateHtmlCertificationDto dto) {
        IDigitalDocument digitalDocument = dto.getDigitalDocument();
        DigitalDocumentUser digitalDocumentUser = dto.getDigitalDocumentUser();

        validateForDocument((DigitalDocument) digitalDocument);
        validateForProxy((IDigitalProxy) digitalDocument);
        validateDigitalDocumentUser(digitalDocumentUser);
    }

    private void validateForDocument(DigitalDocument digitalDocument) {
        if (StringUtils.isBlank(digitalDocument.getCompanyName())) {
            throw new InternalServerException("주식회사명이 존재하지 않습니다.");
        }
    }

    private void validateForProxy(IDigitalProxy digitalProxy) {
        if (StringUtils.isBlank(digitalProxy.getShareholderMeetingName())) {
            throw new InternalServerException("주주총회명이 존재하지 않습니다.");
        }
        if (digitalProxy.getShareholderMeetingDate() == null) {
            throw new InternalServerException("주주총회 일자가 존재하지 않습니다.");
        }

        if (StringUtils.isBlank(digitalProxy.getDesignatedAgentNames())) {
            throw new InternalServerException("수임인지정대리인이 존재하지 않습니다.");
        }
    }

    private void validateDigitalDocumentUser(DigitalDocumentUser grantor) {
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
