package ag.act.entity;

import ag.act.enums.digitaldocument.ZipFileStatus;

import java.time.LocalDateTime;

public interface ZipFileDownload {
    ZipFileStatus getZipFileStatus();

    Long getId();

    Long getRequestUserId();

    String getZipFileKey();

    String getZipFilePath();

    Boolean getIsLatest();

    LocalDateTime getUpdatedAt();
}
