package ag.act.facade.download;

import ag.act.dto.download.DownloadFile;
import ag.act.entity.ZipFileDownload;
import ag.act.enums.FileType;
import ag.act.model.DigitalDocumentZipFileCallbackRequest;

import java.util.Optional;

public interface Downloadable {

    boolean supports(FileType fileType);

    void updateDownloadZipFile(DigitalDocumentZipFileCallbackRequest digitalDocumentZipFileCallbackRequest);

    DownloadFile downloadUserResponseInCsv(Long id);

    DownloadFile downloadZipFile(String zipFileKey);

    Optional<ZipFileDownload> findZipFileDownloadByZipFileKey(String zipFileKey);
}
