package ag.act.module.user.documentdownload;

import ag.act.dto.download.DownloadFile;
import ag.act.entity.User;

public interface UserDocumentDownloader {
    DownloadFile downloadFile(User user);
}
