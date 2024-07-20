package ag.act.service.image;

import ag.act.converter.image.S3PathProvider;
import ag.act.entity.FileContent;
import ag.act.service.aws.S3Service;
import ag.act.service.io.FileContentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ThumbnailImageService {

    private static final String IMAGE_URL_REGEX = "src\\s*=\\s*\"([^\"]+)\"";
    private static final String NO_EXISTING_THUMBNAIL_IMAGE_URL = null;
    private static final int THUMBNAIL_IMAGE_INDEX = 0;

    private final S3Service s3Service;
    private final S3PathProvider s3PathProvider;
    private final ImageResizer imageResizer;
    private final S3PublicImageHandler s3PublicImageHandler;
    private final FileContentService fileContentService;
    private final ThumbnailImagePathProvider thumbnailImagePathProvider;

    @Nullable
    public String generate(List<Long> imageIds, String content) {
        return generate(imageIds, content, NO_EXISTING_THUMBNAIL_IMAGE_URL);
    }

    @Nullable
    public String generate(List<Long> imageIds, String content, String existingThumbnailImageUrl) {
        try {
            return doGenerate(imageIds, content, existingThumbnailImageUrl);
        } catch (Exception e) {
            log.warn("Failed to generate thumbnail image url", e);
            return null;
        }
    }

    private String doGenerate(List<Long> imageIds, String content, String existingThumbnailImageUrl) {
        FileMimeTypeAndUrl firstFileMimeTypeAndUrl = getFirstImageUrl(imageIds, content);
        if (firstFileMimeTypeAndUrl.url() == null) {
            return null;
        }

        if (isSameThumbnailImageUrl(existingThumbnailImageUrl, firstFileMimeTypeAndUrl.url())) {
            return existingThumbnailImageUrl;
        }

        if (firstFileMimeTypeAndUrl.isActImage()) {
            return Optional.ofNullable(resizeImage(firstFileMimeTypeAndUrl))
                .orElse(firstFileMimeTypeAndUrl.url()); // 리사이징 문제시 기존 이미지 URL 을 반환
        }

        // TODO : 게시글 내용에 외부 이미지가 있을 경우 차후에 썸네일을 생성할 수 있도록 처리
        return firstFileMimeTypeAndUrl.url();
    }

    private boolean isActImage(String imageUrl) {
        return s3Service.isActImage(imageUrl);
    }

    private boolean isSameThumbnailImageUrl(String existingThumbnailImageUrl, String firstImageUrl) {
        final String newThumbnailImageUrl = thumbnailImagePathProvider.getThumbnailImageUrl(firstImageUrl);
        return Objects.equals(existingThumbnailImageUrl, newThumbnailImageUrl);
    }

    private FileMimeTypeAndUrl getFirstImageUrl(List<Long> imageIds, String content) {
        if (CollectionUtils.isNotEmpty(imageIds)) {
            return findFileContentByImageId(imageIds.get(THUMBNAIL_IMAGE_INDEX))
                .map(this::createFileMimeTypeAndUrl)
                .orElseGet(this::emptyFileMimeTypeAndUrl);
        }

        final Optional<String> firstImageFullUrlFromContentOptional = getFirstImageUrlFromHtmlContent(content);

        if (firstImageFullUrlFromContentOptional.isEmpty()) {
            return emptyFileMimeTypeAndUrl();
        }

        final String firstImageFullUrl = firstImageFullUrlFromContentOptional.get();

        if (isActImage(firstImageFullUrl)) {
            return findFileContentByFilename(getFilenameFromFullUrl(firstImageFullUrl))
                .map(this::createFileMimeTypeAndUrl)
                .orElseGet(() -> new FileMimeTypeAndUrl(Boolean.TRUE, null, firstImageFullUrl));
        }

        return new FileMimeTypeAndUrl(Boolean.FALSE, null, firstImageFullUrl);
    }

    private String getFilenameFromFullUrl(String imageFullUrl) {
        return imageFullUrl.replace(s3Service.getBaseUrlWithTailingSlash(), "");
    }

    private Optional<FileContent> findFileContentByImageId(Long imageId) {
        return fileContentService.findById(imageId);
    }

    private Optional<FileContent> findFileContentByFilename(String filename) {
        return fileContentService.findByFilename(filename);
    }

    @NotNull
    private FileMimeTypeAndUrl emptyFileMimeTypeAndUrl() {
        return new FileMimeTypeAndUrl(Boolean.FALSE, null, null);
    }

    @NotNull
    private FileMimeTypeAndUrl createFileMimeTypeAndUrl(FileContent fileContent) {
        return new FileMimeTypeAndUrl(
            Boolean.TRUE,
            fileContent.getMimetype(),
            toS3ImageFullUrl(fileContent)
        );
    }

    private String toS3ImageFullUrl(FileContent fileContent) {
        return s3PathProvider.getFullPath(fileContent);
    }

    private Optional<String> getFirstImageUrlFromHtmlContent(String content) {
        final Matcher matcher = Pattern.compile(IMAGE_URL_REGEX).matcher(content);

        return Optional.ofNullable(matcher.find() ? matcher.group(1) : null);
    }

    private String resizeImage(FileMimeTypeAndUrl fileMimeTypeAndUrl) {
        final String originalImageUrl = fileMimeTypeAndUrl.url();

        final Optional<BufferedImage> originalImageOptional = readImageFromS3(originalImageUrl);
        if (originalImageOptional.isEmpty()) {
            return originalImageUrl;
        }

        final BufferedImage originalImage = originalImageOptional.get();
        if (!imageResizer.isResizeNeeded(originalImage)) {
            return originalImageUrl;
        }

        final Optional<ImageResizeResult> resizedByteStream = imageResizer.resize(originalImage);

        if (resizedByteStream.isEmpty()) {
            return originalImageUrl;
        }

        ImageResizeResult imageResizeResult = resizedByteStream.get();
        return uploadImageToS3(
            thumbnailImagePathProvider.getThumbnailImageUrl(originalImageUrl),
            imageResizeResult.extension(),
            imageResizeResult.toByteArray()
        ).orElse(originalImageUrl);
    }

    @NotNull
    private Optional<String> uploadImageToS3(String imageFullPath, String mimeType, byte[] byteArray) {
        return s3PublicImageHandler.uploadImage(imageFullPath, mimeType, byteArray);
    }

    private Optional<BufferedImage> readImageFromS3(String imageFullPath) {
        return s3PublicImageHandler.findImage(imageFullPath);
    }
}
