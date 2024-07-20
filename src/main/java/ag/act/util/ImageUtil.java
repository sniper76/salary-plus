package ag.act.util;

import ag.act.enums.FileType;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.UUID;

public class ImageUtil {
    public static String refineFileName(FileType fileType, String fileName) {
        final String extension = FilenameUtils.getExtension(fileName);
        final UUID uuid = UUID.randomUUID();
        final LocalDateTime now = LocalDateTime.now();

        return "%s/%s/%s/%s.%s".formatted(
            fileType.getPathPrefix(),
            now.getYear(),
            StringUtils.leftPad(String.valueOf(now.getMonthValue()), 2, '0'),
            uuid,
            extension
        );
    }

    public static String getFullPath(String baseUrl, String fileName) {
        return "%s/%s".formatted(baseUrl, fileName);
    }
}
