package ag.act.module.digitaldocumentgenerator.dto;

import ag.act.enums.digitaldocument.IdCardWatermarkType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@Builder
public class AttachingFilesDto {
    private IdCardWatermarkType idCardWatermarkType;
    private MultipartFile idCardImage;
    private List<MultipartFile> bankAccountImages;
    private MultipartFile hectoEncryptedBankAccountPdf;
}
