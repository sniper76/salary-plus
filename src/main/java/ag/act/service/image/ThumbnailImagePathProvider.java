package ag.act.service.image;

import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ThumbnailImagePathProvider {
    private static final String THUMBNAIL_PATH_TEMPLATE = "%s%s_thumbnail.%s";

    public String getThumbnailImageUrl(String originalPath) {
        String pathWithTailingSlash = FilenameUtils.getFullPath(originalPath);
        String basename = FilenameUtils.getBaseName(originalPath);
        String extension = FilenameUtils.getExtension(originalPath);

        return THUMBNAIL_PATH_TEMPLATE.formatted(pathWithTailingSlash, basename, extension);
    }
}
