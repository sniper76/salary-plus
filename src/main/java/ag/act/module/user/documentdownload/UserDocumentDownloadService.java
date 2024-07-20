package ag.act.module.user.documentdownload;

import ag.act.dto.download.DownloadFile;
import ag.act.entity.User;
import ag.act.module.user.documentdownload.solidarityleaderconfidentialagreement.UserSolidarityLeaderConfidentialAgreementDocumentDownloader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserDocumentDownloadService {
    private final UserSolidarityLeaderConfidentialAgreementDocumentDownloader userSolidarityLeaderConfidentialAgreementDocumentDownloader;

    public DownloadFile downloadSolidarityLeaderConfidentialAgreementDocument(User user) {
        return userSolidarityLeaderConfidentialAgreementDocumentDownloader.downloadFile(user);
    }
}
