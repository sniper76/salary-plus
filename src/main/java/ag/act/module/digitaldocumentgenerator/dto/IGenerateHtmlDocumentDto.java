package ag.act.module.digitaldocumentgenerator.dto;

import ag.act.entity.digitaldocument.DigitalDocumentUser;
import ag.act.entity.digitaldocument.IDigitalDocument;
import org.springframework.web.multipart.MultipartFile;

public interface IGenerateHtmlDocumentDto {
    default IDigitalDocument getDigitalDocument() {
        return null;
    }

    default void setDigitalDocument(IDigitalDocument digitalDocument) {
    }

    default DigitalDocumentUser getDigitalDocumentUser() {
        return null;
    }

    default void setDigitalDocumentUser(DigitalDocumentUser digitalDocumentUser) {
    }

    default MultipartFile getSignatureImage() {
        return null;
    }

    default void setSignatureImage(MultipartFile signatureImage) {
    }

    default AttachingFilesDto getAttachingFilesDto() {
        return null;
    }

    default void setAttachingFilesDto(AttachingFilesDto attachingFilesDto) {
    }
}