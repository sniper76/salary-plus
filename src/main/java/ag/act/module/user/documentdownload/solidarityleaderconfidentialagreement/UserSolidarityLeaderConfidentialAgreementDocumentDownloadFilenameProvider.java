package ag.act.module.user.documentdownload.solidarityleaderconfidentialagreement;

import ag.act.dto.file.FilenameDto;
import ag.act.entity.User;
import ag.act.util.FilenameUtil;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;

@Component
public class UserSolidarityLeaderConfidentialAgreementDocumentDownloadFilenameProvider {

    public FilenameDto getSolidarityLeaderConfidentialAgreementDocumentFilenameDto(User user) {
        final String fullPath = FilenameUtil.getSolidarityLeaderConfidentialAgreementDocumentPath(user.getId(), user.getName());
        final String filename = FilenameUtils.getName(fullPath);
        final String baseName = FilenameUtils.getBaseName(fullPath);

        return new FilenameDto(filename, baseName, fullPath);
    }
}
