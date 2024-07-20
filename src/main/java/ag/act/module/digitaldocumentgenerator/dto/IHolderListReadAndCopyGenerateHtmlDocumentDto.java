package ag.act.module.digitaldocumentgenerator.dto;

import ag.act.dto.user.HolderListReadAndCopyDataModel;
import ag.act.entity.digitaldocument.IDigitalDocument;
import org.springframework.web.multipart.MultipartFile;

public interface IHolderListReadAndCopyGenerateHtmlDocumentDto extends IGenerateHtmlDocumentDto {

    IDigitalDocument getDigitalDocument();

    MultipartFile getSignatureImage();

    AttachingFilesDto getAttachingFilesDto();

    void setDigitalDocument(IDigitalDocument digitalDocument);

    void setSignatureImage(MultipartFile signatureImage);

    void setAttachingFilesDto(AttachingFilesDto attachingFilesDto);

    HolderListReadAndCopyDataModel getHolderListReadAndCopyDataModel();

    void setHolderListReadAndCopyDataModel(HolderListReadAndCopyDataModel digitalDocumentUser);
}
