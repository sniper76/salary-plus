package ag.act.module.digitaldocumentgenerator.validator;

import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.entity.digitaldocument.DigitalDocumentUser;
import ag.act.exception.InternalServerException;
import ag.act.module.digitaldocumentgenerator.dto.IGenerateHtmlDocumentDto;
import ag.act.validator.ImageMediaTypeValidator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

public abstract class BaseDigitalDocumentFillValidator {

    @Autowired
    private ImageMediaTypeValidator imageMediaTypeValidator;

    public final void validate(IGenerateHtmlDocumentDto dto) {
        validateGeneric(dto);
        validateTypeSpecific(dto);
    }

    protected abstract void validateTypeSpecific(IGenerateHtmlDocumentDto dto);

    private void validateGeneric(IGenerateHtmlDocumentDto dto) {
        validateCompanyName((DigitalDocument) dto.getDigitalDocument());
        validateGrantor(dto.getDigitalDocumentUser());
        if (dto.getSignatureImage() != null) {
            validateSignatureImage(dto.getSignatureImage());
        }
    }

    protected void validateGrantor(DigitalDocumentUser grantor) {
    }

    protected void validateCompanyName(DigitalDocument digitalDocument) {
        if (StringUtils.isBlank(digitalDocument.getCompanyName())) {
            throw new InternalServerException("주식회사명이 존재하지 않습니다.");
        }
    }

    protected void validateSignatureImage(MultipartFile signatureImage) {
        imageMediaTypeValidator.validate(signatureImage);
    }
}
