package ag.act.handler;

import ag.act.api.UserDigitalDocumentApiDelegate;
import ag.act.core.guard.IsActiveUserGuard;
import ag.act.core.guard.UseGuards;
import ag.act.facade.digitaldocument.DigitalDocumentFacade;
import ag.act.model.SimpleStringResponse;
import ag.act.model.UserDigitalDocumentResponse;
import ag.act.util.DownloadFileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@UseGuards({IsActiveUserGuard.class})
public class UserDigitalDocumentApiDelegateImpl implements UserDigitalDocumentApiDelegate {
    private final DigitalDocumentFacade digitalDocumentFacade;

    @Override
    public ResponseEntity<UserDigitalDocumentResponse> createUserDigitalDocumentWithImage(
        Long digitalDocumentId, MultipartFile signImage, MultipartFile idCardImage,
        List<MultipartFile> bankAccountImages, MultipartFile hectoEncryptedBankAccountPdf, String answerData
    ) {
        return ResponseEntity.ok(digitalDocumentFacade.createUserDigitalDocumentWithImage(
            digitalDocumentId, signImage, idCardImage, bankAccountImages, hectoEncryptedBankAccountPdf, answerData
        ));
    }

    @Override
    public ResponseEntity<SimpleStringResponse> updateUserDigitalDocumentStatus(Long digitalDocumentId) {
        return ResponseEntity.ok(digitalDocumentFacade.updateUserDigitalDocumentStatus(digitalDocumentId));
    }

    @Override
    public ResponseEntity<SimpleStringResponse> deleteUserDigitalDocument(Long digitalDocumentId) {
        return ResponseEntity.ok(digitalDocumentFacade.deleteUserDigitalDocument(digitalDocumentId));
    }

    @Override
    public ResponseEntity<Resource> getUserDigitalDocumentPdf(Long digitalDocumentId) {
        return DownloadFileUtil.ok(digitalDocumentFacade.getUserDigitalDocumentPdf(digitalDocumentId));
    }
}
