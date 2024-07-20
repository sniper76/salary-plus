package ag.act.converter.digitaldocument;

import ag.act.converter.DateTimeConverter;
import ag.act.entity.ZipFileDownload;
import org.springframework.stereotype.Component;

@Component
public class DigitalDocumentDownloadResponseConverter {
    public ag.act.model.DigitalDocumentDownloadResponse convert(ZipFileDownload zipFileDownload) {
        if (zipFileDownload == null) {
            return null;
        }

        return new ag.act.model.DigitalDocumentDownloadResponse()
            .id(zipFileDownload.getId())
            .requestUserId(zipFileDownload.getRequestUserId())
            .zipFileKey(zipFileDownload.getZipFileKey())
            .zipFileStatus(zipFileDownload.getZipFileStatus().name())
            .updatedAt(DateTimeConverter.convert(zipFileDownload.getUpdatedAt()));
    }
}
