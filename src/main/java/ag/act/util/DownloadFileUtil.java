package ag.act.util;

import ag.act.dto.download.DownloadFile;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class DownloadFileUtil {

    public static final String ATTACHMENT_FILENAME_FORMAT = "attachment; filename=\"%s\"";

    public static ResponseEntity<Resource> ok(DownloadFile downloadFile) {

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, getFormattedFileName(downloadFile.getFileName()));

        return ResponseEntity.ok()
            .contentType(downloadFile.getContentType())
            .headers(headers)
            .body(downloadFile.getResource());
    }

    public static String getDownloadFileName(String fileName) {
        return URLEncoder.encode(fileName, StandardCharsets.UTF_8);
    }

    public static void setFilename(HttpServletResponse response, String fileName) {
        response.addHeader(HttpHeaders.CONTENT_DISPOSITION, getFormattedFileName(fileName));
    }

    private static String getFormattedFileName(String fileName) {
        return ATTACHMENT_FILENAME_FORMAT.formatted(getDownloadFileName(fileName));
    }
}
