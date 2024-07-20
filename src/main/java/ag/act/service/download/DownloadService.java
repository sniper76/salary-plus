package ag.act.service.download;

import ag.act.core.holder.RequestContextHolder;
import ag.act.dto.download.DownloadFile;
import ag.act.exception.BadRequestException;
import ag.act.service.aws.S3Service;
import ag.act.util.DownloadFileUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@RequiredArgsConstructor
@Service
public class DownloadService {

    private final S3Service s3Service;

    public DownloadFile downloadFile(String fileKey, String filename, String mimeType) {
        final HttpServletResponse response = RequestContextHolder.getResponse();

        try (final InputStream inputStream = getInputStream(fileKey)) {

            DownloadFileUtil.setFilename(response, filename);
            response.setHeader(HttpHeaders.CONTENT_TYPE, mimeType);

            inputStream.transferTo(response.getOutputStream());

        } catch (IOException e) {
            log.error("Failed to download file: {}", e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        return DownloadFile.builder().fileName(filename).build();
    }

    private InputStream getInputStream(String fileKey) {
        try {
            return s3Service.readObject(fileKey);
        } catch (Exception e) {
            throw new BadRequestException("파일을 찾을 수 없습니다.", e);
        }
    }
}
