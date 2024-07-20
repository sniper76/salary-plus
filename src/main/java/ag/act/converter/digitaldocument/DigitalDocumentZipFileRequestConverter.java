package ag.act.converter.digitaldocument;

import ag.act.core.infra.S3Environment;
import ag.act.dto.DigitalDocumentZipFileRequest;
import ag.act.enums.FileType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DigitalDocumentZipFileRequestConverter {
    private final S3Environment s3Environment;

    public DigitalDocumentZipFileRequest convert(
        Long digitalDocumentId,
        Long digitalDocumentDownloadId,
        String password,
        String zipFileName
    ) {
        return convert(
            digitalDocumentId,
            List.of(digitalDocumentId),
            digitalDocumentDownloadId,
            password,
            zipFileName,
            FileType.DIGITAL_DOCUMENT
        );
    }

    public DigitalDocumentZipFileRequest convert(
        Long destinationId,
        List<Long> sourceIds,
        Long downloadId,
        String password,
        String zipFilename,
        FileType fileType
    ) {
        final String privateBucketName = s3Environment.getPrivateBucketName();
        final String destinationPathPrefix = fileType.getPathPrefix();

        return DigitalDocumentZipFileRequest.builder()
            .sourceBucketName(privateBucketName)
            .destinationBucketName(privateBucketName)
            .sourceDirectories(createSourceDirectories(sourceIds))
            .destinationDirectory("%s/%s/destination".formatted(destinationPathPrefix, destinationId))
            .digitalDocumentDownloadId(downloadId)
            .password(password)
            .zipFilename(zipFilename)
            .fileType(fileType.name())
            .build();
    }

    private List<String> createSourceDirectories(List<Long> digitalDocumentIds) {
        return digitalDocumentIds.stream()
            .map(it -> "%s/%s/source".formatted(FileType.DIGITAL_DOCUMENT.getPathPrefix(), it))
            .toList();
    }
}
