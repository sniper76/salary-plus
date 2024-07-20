package ag.act.facade;

import ag.act.converter.image.DefaultImageResponseConverter;
import ag.act.converter.image.ImageResponseConverter;
import ag.act.entity.FileContent;
import ag.act.entity.User;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.enums.FileContentType;
import ag.act.enums.FileType;
import ag.act.exception.InternalServerException;
import ag.act.module.digitaldocumentgenerator.DigitalDocumentFilenameGenerator;
import ag.act.service.aws.S3Service;
import ag.act.service.io.FileContentService;
import ag.act.service.io.UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class FileFacade {

    private final UploadService uploadService;
    private final S3Service s3Service;
    private final FileContentService fileContentService;
    private final ImageResponseConverter imageResponseConverter;
    private final DefaultImageResponseConverter defaultImageResponseConverter;
    private final DigitalDocumentFilenameGenerator digitalDocumentFilenameGenerator;

    public ag.act.model.ImageUploadResponse uploadImage(
        MultipartFile file,
        FileContentType fileContentType,
        FileType fileType
    ) {
        return uploadImage(file, fileContentType, fileType, null);
    }

    public ag.act.model.ImageUploadResponse uploadImage(
        MultipartFile file,
        FileContentType fileContentType,
        FileType fileType,
        String description
    ) {

        final Optional<FileContent> fileContentOptional = uploadService.uploadFile(file, fileContentType, fileType, description);

        final FileContent fileContent = fileContentOptional
            .orElseThrow(() -> new InternalServerException("이미지를 저장하는 중에 오류가 발생하였습니다."));

        return imageResponseConverter.convert(imageResponseConverter.convert(fileContent));
    }

    @Async
    public void uploadMyDataJson(String jsonData, Long userId) {
        try {
            uploadService.uploadMyData(jsonData, userId);
        } catch (Exception e) {
            throw new InternalServerException("마이데이터 JSON 파일을 저장하는 중에 오류가 발생하였습니다.", e);
        }
    }

    public String uploadDigitalDocument(byte[] pdfBytes, DigitalDocument digitalDocument, Long userId) {
        try {
            final String originalFilename = digitalDocumentFilenameGenerator.generate(digitalDocument, userId);
            return uploadService.uploadDigitalDocument(pdfBytes, digitalDocument, originalFilename);
        } catch (Exception e) {
            throw new InternalServerException("전자문서 파일을 저장하는 중에 오류가 발생하였습니다.", e);
        }
    }

    public String uploadSolidarityLeaderConfidentialAgreement(byte[] pdfBytes, User user) {
        try {
            return uploadService.uploadSolidarityLeaderConfidentialAgreement(pdfBytes, user);
        } catch (Exception e) {
            throw new InternalServerException("주주대표 비밀유지 서약서 파일을 저장하는 중에 오류가 발생하였습니다.", e);
        }
    }

    @SuppressWarnings("UnusedReturnValue")
    public String uploadDigitalDocumentCertification(byte[] pdfBytes, DigitalDocument digitalDocument, Long userId) {
        try {
            final String originalFilename = digitalDocumentFilenameGenerator.generateCertificationFilename(digitalDocument, userId);
            return uploadService.uploadDigitalDocumentCertification(pdfBytes, digitalDocument, originalFilename);
        } catch (Exception e) {
            throw new InternalServerException("전자문서 인증서 파일을 저장하는 중에 오류가 발생하였습니다.", e);
        }
    }

    public ag.act.model.DefaultProfileImagesResponse getDefaultProfileImages() {
        final List<FileContent> images = fileContentService.getDefaultProfileImages();

        return defaultImageResponseConverter.convert(images.stream().map(defaultImageResponseConverter::convert).toList());
    }

    public void deleteFile(FileContent deletedFileContent) {
        s3Service.removeObject(deletedFileContent.getFileType(), deletedFileContent.getFilename());
    }
}
