package ag.act.module.digitaldocumentgenerator.document;

import ag.act.dto.digitaldocument.PdfDataDto;
import ag.act.enums.FileContentType;
import ag.act.enums.FileType;
import ag.act.exception.InternalServerException;
import ag.act.service.io.UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class DigitalDocumentUploadService {
    private final UploadService uploadService;

    public String uploadPdf(PdfDataDto pdfDataDto, String fullPath, String originalFilename) {
        try {
            return uploadService.uploadPdf(
                pdfDataDto.getPdfBytes(),
                fullPath,
                originalFilename,
                FileType.DIGITAL_DOCUMENT
            );
        } catch (Exception e) {
            throw new InternalServerException("전자문서 파일을 저장하는 중에 오류가 발생하였습니다.", e);
        }
    }

    public void uploadMultipartFile(MultipartFile file, String fullPath, String originalFilename) {
        try {
            uploadService.uploadFile(
                file,
                FileContentType.DEFAULT,
                FileType.DIGITAL_DOCUMENT,
                fullPath,
                originalFilename
            );
        } catch (Exception e) {
            throw new InternalServerException("%s 파일을 저장하는 중에 오류가 발생하였습니다.".formatted(originalFilename), e);
        }
    }
}
