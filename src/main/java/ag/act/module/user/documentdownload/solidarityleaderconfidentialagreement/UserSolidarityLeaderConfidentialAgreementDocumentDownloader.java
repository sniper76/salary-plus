package ag.act.module.user.documentdownload.solidarityleaderconfidentialagreement;

import ag.act.dto.download.DownloadFile;
import ag.act.dto.file.FilenameDto;
import ag.act.entity.User;
import ag.act.module.user.documentdownload.UserDocumentDownloader;
import ag.act.service.aws.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Optional;

@SuppressWarnings({"LineLength"})
@Component
@RequiredArgsConstructor
public class UserSolidarityLeaderConfidentialAgreementDocumentDownloader implements UserDocumentDownloader {
    private final S3Service s3Service;
    private final UserSolidarityLeaderConfidentialAgreementDocumentDownloadFilenameProvider filenameProvider;
    private final UserSolidarityLeaderConfidentialAgreementDocumentDownloadValidator validator;

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Override
    public DownloadFile downloadFile(User user) {

        validator.validateUser(user);

        final FilenameDto filenameDto = filenameProvider.getSolidarityLeaderConfidentialAgreementDocumentFilenameDto(user);
        final Optional<InputStream> inputStreamOptional = s3Service.findObjectFromPrivateBucket(filenameDto.fullPath());

        validator.validateFile(user, inputStreamOptional);

        return DownloadFile.builder()
            .resource(new InputStreamResource(inputStreamOptional.get()))
            .fileName(filenameDto.filename())
            .contentType(MediaType.APPLICATION_PDF)
            .build();
    }
}
