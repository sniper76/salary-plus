package ag.act.handler.admin;

import ag.act.api.AdminUserDigitalDocumentApiDelegate;
import ag.act.core.guard.IsCmsUserGuard;
import ag.act.core.guard.UseGuards;
import ag.act.facade.digitaldocument.DigitalDocumentFacade;
import ag.act.util.DownloadFileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminUserDigitalDocumentApiDelegateImpl implements AdminUserDigitalDocumentApiDelegate {
    private final DigitalDocumentFacade digitalDocumentFacade;

    @Override
    @UseGuards({IsCmsUserGuard.class})
    public ResponseEntity<Resource> getUserDigitalDocumentPdfByAdmin(Long userId, Long digitalDocumentId) {
        return DownloadFileUtil.ok(digitalDocumentFacade.getUserDigitalDocumentPdf(digitalDocumentId, userId));
    }

}
