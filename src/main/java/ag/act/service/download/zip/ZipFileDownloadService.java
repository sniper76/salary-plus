package ag.act.service.download.zip;


import ag.act.dto.download.DownloadFile;
import ag.act.entity.ZipFileDownload;
import ag.act.service.download.DownloadService;
import ag.act.validator.ZipFileDownloadValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ZipFileDownloadService {
    private final DownloadService downloadService;
    private final ZipFileDownloadValidator zipFileDownloadValidator;

    public DownloadFile downloadZipFile(ZipFileDownload zipFileDownload) {

        zipFileDownloadValidator.validate(zipFileDownload);

        final String zipFilePath = zipFileDownload.getZipFilePath();
        final String filename = FilenameUtils.getName(zipFilePath);

        return downloadService.downloadFile(zipFilePath, filename, MediaType.APPLICATION_OCTET_STREAM_VALUE);
    }
}
