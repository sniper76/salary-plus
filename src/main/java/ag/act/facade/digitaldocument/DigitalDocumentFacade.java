package ag.act.facade.digitaldocument;

import ag.act.dto.download.DownloadFile;
import ag.act.entity.Post;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.enums.DigitalDocumentType;
import ag.act.model.SimpleStringResponse;
import ag.act.model.Status;
import ag.act.model.UserDigitalDocumentResponse;
import ag.act.service.admin.stock.acceptor.StockAcceptorUserHistoryService;
import ag.act.service.aws.S3Service;
import ag.act.service.digitaldocument.DigitalDocumentUserService;
import ag.act.service.post.PostService;
import ag.act.util.FilenameUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DigitalDocumentFacade {
    private final StockAcceptorUserHistoryService stockAcceptorUserHistoryService;
    private final DigitalDocumentUserService digitalDocumentUserService;
    private final S3Service s3Service;
    private final PostService postService;

    public UserDigitalDocumentResponse createUserDigitalDocumentWithImage(
        Long digitalDocumentId,
        MultipartFile signImage,
        MultipartFile idCardImage,
        List<MultipartFile> bankAccountImages,
        MultipartFile hectoEncryptedBankAccountPdf,
        String answerData
    ) {
        return digitalDocumentUserService.createUserDigitalDocumentWithImage(
            signImage, idCardImage, bankAccountImages, hectoEncryptedBankAccountPdf, answerData, digitalDocumentId
        );
    }

    public SimpleStringResponse updateUserDigitalDocumentStatus(Long digitalDocumentId) {
        return digitalDocumentUserService.updateUserDigitalDocumentStatus(
            digitalDocumentId
        );
    }

    public SimpleStringResponse deleteUserDigitalDocument(
        Long digitalDocumentId
    ) {
        return digitalDocumentUserService.deleteUserDigitalDocument(
            digitalDocumentId
        );
    }

    public DownloadFile getUserDigitalDocumentPdf(
        Long digitalDocumentId
    ) {
        return digitalDocumentUserService.getUserDigitalDocumentPdf(
            digitalDocumentId
        );
    }

    public DownloadFile getUserDigitalDocumentPdf(Long digitalDocumentId, Long userId) {
        return digitalDocumentUserService.getUserDigitalDocumentPdf(digitalDocumentId, userId);
    }

    public void deleteOldDigitalDocuments(DigitalDocument digitalDocument) {
        final Post post = digitalDocument.getPost();

        if (digitalDocument.getType() != DigitalDocumentType.ETC_DOCUMENT) {
            stockAcceptorUserHistoryService.replaceUserInfoWithDummy(digitalDocument);
        }

        final String documentIdPath = FilenameUtil.getDigitalDocumentIdPath(digitalDocument.getId());
        s3Service.deleteDirectoryInFiles(documentIdPath);

        post.setStatus(Status.INACTIVE_BY_ADMIN);
        digitalDocument.setStatus(Status.INACTIVE_BY_ADMIN);
        post.setDigitalDocument(digitalDocument);
        postService.savePost(post);
    }
}
