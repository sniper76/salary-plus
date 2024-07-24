package ag.act.module.digitaldocumentgenerator.dto;

import ag.act.dto.user.HolderListReadAndCopyDataModel;
import ag.act.entity.digitaldocument.DigitalDocumentUser;
import ag.act.entity.digitaldocument.IDigitalDocument;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HolderListReadAndCopyGenerateHtmlDocumentDto implements IGenerateHtmlDocumentDto, IHolderListReadAndCopyGenerateHtmlDocumentDto {
    private IDigitalDocument digitalDocument;
    private DigitalDocumentUser digitalDocumentUser;
    private MultipartFile signatureImage;
    private AttachingFilesDto attachingFilesDto;
    private HolderListReadAndCopyDataModel holderListReadAndCopyDataModel;
}
