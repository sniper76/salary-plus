package ag.act.handler.admin.user;

import ag.act.api.AdminUserDownloadDocumentApiDelegate;
import ag.act.facade.user.UserFacade;
import ag.act.util.DownloadFileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminUserDownloadDocumentApiDelegateImpl implements AdminUserDownloadDocumentApiDelegate {

    private final UserFacade userFacade;

    @Override
    public ResponseEntity<Resource> getSolidarityLeaderConfidentialAgreementDocumentByAdmin(Long userId) {
        return DownloadFileUtil.ok(userFacade.getSolidarityLeaderConfidentialAgreementDocument(userId));
    }
}
