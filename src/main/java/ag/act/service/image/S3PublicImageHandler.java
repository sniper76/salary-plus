package ag.act.service.image;

import ag.act.converter.image.S3PathProvider;
import ag.act.service.aws.S3Service;
import ag.act.service.io.UploadService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class S3PublicImageHandler {
    private final S3PathProvider s3PathProvider;
    private final S3Service s3Service;
    private final ImageIOWrapper imageIOWrapper;
    private final UploadService uploadService;

    public Optional<BufferedImage> findImage(String imageFullPath) {
        final String fileKey = s3PathProvider.getContentPath(imageFullPath);
        return s3Service.findObjectFromPublicBucket(fileKey)
            .flatMap(imageIOWrapper::read);
    }

    public Optional<String> uploadImage(
        String imageFullPath,
        String mimeType,
        byte[] byteArray
    ) {
        try {
            uploadService.uploadImageToPublicBucket(
                s3PathProvider.getContentPath(imageFullPath),
                mimeType,
                byteArray
            );
            return Optional.of(imageFullPath);
        } catch (Exception e) {
            log.error("Failed to upload image", e);
            return Optional.empty();
        }
    }
}
