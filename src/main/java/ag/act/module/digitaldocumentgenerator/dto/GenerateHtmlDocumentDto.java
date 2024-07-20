package ag.act.module.digitaldocumentgenerator.dto;

import ag.act.entity.digitaldocument.DigitalDocumentUser;
import ag.act.entity.digitaldocument.IDigitalDocument;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
public class GenerateHtmlDocumentDto implements IGenerateHtmlDocumentDto {
    private IDigitalDocument digitalDocument;
    private DigitalDocumentUser digitalDocumentUser;
    private MultipartFile signatureImage;
    private AttachingFilesDto attachingFilesDto;
    private String digitalDocumentNo;
}
