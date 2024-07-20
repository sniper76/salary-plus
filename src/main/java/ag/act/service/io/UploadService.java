package ag.act.service.io;

import ag.act.dto.file.UploadFilePathDto;
import ag.act.entity.FileContent;
import ag.act.entity.User;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.enums.FileContentType;
import ag.act.enums.FileType;
import ag.act.service.aws.S3Service;
import ag.act.util.FilenameUtil;
import ag.act.util.ImageUtil;
import ag.act.util.KoreanDateTimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UploadService {

    private static final String CSV_MIME_TYPE = "text/csv";
    private final S3Service s3Service;
    private final FileContentService fileContentService;

    public Optional<FileContent> uploadFile(
        MultipartFile file,
        FileContentType fileContentType,
        FileType fileType,
        String description
    ) {
        final String uploadPath = ImageUtil.refineFileName(fileType, file.getOriginalFilename());
        return uploadFile(file, fileContentType, fileType, uploadPath, description);
    }

    public Optional<FileContent> uploadFile(
        MultipartFile file,
        FileContentType fileContentType,
        FileType fileType,
        String uploadPath,
        String description
    ) {
        final UploadFilePathDto uploadFilePathDto = UploadFilePathDto.builder()
            .uploadPath(uploadPath)
            .originalPath(file.getOriginalFilename())
            .mimeType(file.getContentType())
            .fileType(fileType)
            .fileSize(file.getSize())
            .build();

        try {
            var uploaded = s3Service.putObject(uploadFilePathDto, file.getInputStream());
            if (uploaded) {
                return Optional.ofNullable(fileContentService.saveFileContent(
                    uploadFilePathDto,
                    fileContentType,
                    fileType,
                    description,
                    ag.act.model.Status.ACTIVE
                ));
            }
        } catch (IOException e) {
            log.error("File upload error", e);
        }
        return Optional.empty();
    }

    public Optional<FileContent> uploadFileByte(
        byte[] fileBytes,
        String fileName,
        FileContentType fileContentType,
        FileType fileType
    ) {
        try {
            final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(fileBytes);
            final UploadFilePathDto uploadFilePathDto = UploadFilePathDto.builder()
                .uploadPath(ImageUtil.refineFileName(fileType, fileName))
                .originalPath(fileName)
                .mimeType(MediaType.IMAGE_PNG_VALUE)
                .fileType(fileType)
                .fileSize(fileBytes.length)
                .build();

            boolean uploaded = s3Service.putObject(uploadFilePathDto, byteArrayInputStream);
            if (uploaded) {
                return Optional.ofNullable(fileContentService.saveFileContent(
                    uploadFilePathDto,
                    fileContentType,
                    fileType,
                    null,
                    ag.act.model.Status.ACTIVE
                ));
            }
        } catch (Exception e) {
            log.error("Image file upload error", e);
        }
        return Optional.empty();
    }

    public void uploadMyData(String jsonData, Long userId) {
        final String currentDateTime = KoreanDateTimeUtil.getFormattedCurrentDateTime("yyyy_MM_dd_HH_mm_ss");

        final String originalFilename = "%s_%s.json".formatted(userId, currentDateTime);
        final UploadFilePathDto uploadFilePathDto = UploadFilePathDto.builder()
            .uploadPath("%s/%s/%s".formatted(FileType.MY_DATA.getPathPrefix(), userId, originalFilename))
            .originalPath(originalFilename)
            .mimeType(MediaType.APPLICATION_JSON_VALUE)
            .fileType(FileType.MY_DATA)
            .fileSize(jsonData.getBytes().length)
            .build();

        s3Service.putObject(uploadFilePathDto, jsonData);
    }

    public String uploadDigitalDocument(byte[] pdfBytes, DigitalDocument digitalDocument, String originalFilename) {
        final String fullPath = FilenameUtil.getDigitalDocumentUploadPath(digitalDocument.getId(), originalFilename);

        return uploadPdf(pdfBytes, fullPath, originalFilename, FileType.DIGITAL_DOCUMENT);
    }

    public String uploadSolidarityLeaderConfidentialAgreement(byte[] pdfBytes, User user) {
        final String fullPath = FilenameUtil.getSolidarityLeaderConfidentialAgreementDocumentPath(user.getId(), user.getName());
        final String filename = FilenameUtils.getName(fullPath);

        return uploadPdf(pdfBytes, fullPath, filename, FileType.SOLIDARITY_LEADER_CONFIDENTIAL_AGREEMENT_SIGN);
    }

    public String uploadDigitalDocumentCertification(byte[] pdfBytes, DigitalDocument digitalDocument, String originalFilename) {
        final String fullPath = FilenameUtil.getDigitalDocumentUploadPath(digitalDocument.getId(), originalFilename);

        return uploadPdf(pdfBytes, fullPath, originalFilename, FileType.DIGITAL_DOCUMENT);
    }

    public String uploadPdf(byte[] pdfBytes, String fullPath, String originalFilename, FileType fileType) {
        final UploadFilePathDto uploadFilePathDto = UploadFilePathDto.builder()
            .uploadPath(fullPath)
            .originalPath(originalFilename)
            .mimeType(MediaType.APPLICATION_PDF_VALUE)
            .fileType(fileType)
            .fileSize(pdfBytes.length)
            .build();

        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(pdfBytes);
        s3Service.putObject(uploadFilePathDto, byteArrayInputStream);

        return uploadFilePathDto.getUploadPath();
    }

    public String uploadCsv(byte[] csvBytes, String fullPath) {
        final UploadFilePathDto uploadFilePathDto = UploadFilePathDto.builder()
            .uploadPath(fullPath)
            .mimeType(CSV_MIME_TYPE)
            .fileType(FileType.MATRIX)
            .fileSize(csvBytes.length)
            .build();

        s3Service.putObject(uploadFilePathDto, new ByteArrayInputStream(csvBytes));

        return uploadFilePathDto.getUploadPath();
    }

    @NotNull
    public String uploadImageToPublicBucket(
        String contentPath,
        String mimeType,
        byte[] byteArray
    ) {
        InputStream inputStream = new ByteArrayInputStream(byteArray);

        final UploadFilePathDto uploadFilePathDto = UploadFilePathDto.builder()
            .uploadPath(contentPath)
            .originalPath(FilenameUtils.getName(contentPath))
            .mimeType(mimeType)
            .fileType(FileType.IMAGE)
            .fileSize(byteArray.length)
            .build();

        s3Service.putObject(uploadFilePathDto, inputStream);

        return uploadFilePathDto.getUploadPath();
    }
}
