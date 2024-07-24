package ag.act.module.digitaldocumentgenerator.dto;

import ag.act.dto.user.HolderListReadAndCopyDataModel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
public class GenerateDigitalDocumentDto {
    private MultipartFile signatureImage;
    private AttachingFilesDto attachingFilesDto;
    private HolderListReadAndCopyDataModel holderListReadAndCopyDataModel;
}
