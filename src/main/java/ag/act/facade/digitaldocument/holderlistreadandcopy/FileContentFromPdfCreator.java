package ag.act.facade.digitaldocument.holderlistreadandcopy;

import ag.act.dto.digitaldocument.PdfDataDto;
import ag.act.entity.FileContent;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.enums.FileContentType;
import ag.act.enums.FileType;
import ag.act.exception.BadRequestException;
import ag.act.service.image.ImageExtractor;
import ag.act.service.io.UploadService;
import ag.act.service.post.PostImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FileContentFromPdfCreator {
    private final ImageExtractor imageExtractor;
    private final UploadService uploadService;
    private final PostImageService postImageService;

    public FileContent create(PdfDataDto pdfDataDto, DigitalDocument digitalDocument, Long postId) {
        final byte[] firstImageBytes = imageExtractor.extractPdfFirstPageImage(pdfDataDto.getPdfMaskingBytes());

        final String fileName = createFilename(digitalDocument);

        final FileContent fileContent = uploadFile(firstImageBytes, fileName);

        postImageService.savePostImageList(postId, List.of(fileContent.getId()));

        return fileContent;
    }

    private FileContent uploadFile(byte[] firstImageBytes, String fileName) {
        return uploadService.uploadFileByte(
            firstImageBytes, fileName, FileContentType.DEFAULT, FileType.IMAGE
        ).orElseThrow(() -> new BadRequestException("주주명부 열람/등사 이미지 생성에 실패했습니다."));
    }

    private String createFilename(DigitalDocument digitalDocument) {
        return "%s_%s_%s.png".formatted(digitalDocument.getStockCode(), digitalDocument.getId(), digitalDocument.getType());
    }
}
