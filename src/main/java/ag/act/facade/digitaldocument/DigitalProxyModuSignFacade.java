package ag.act.facade.digitaldocument;

import ag.act.configuration.security.ActUserProvider;
import ag.act.entity.DigitalProxy;
import ag.act.entity.DigitalProxyApproval;
import ag.act.entity.Post;
import ag.act.entity.User;
import ag.act.module.modusign.ModuSignDocument;
import ag.act.module.modusign.ModuSignService;
import ag.act.service.digitaldocument.modusign.DigitalProxyApprovalService;
import ag.act.util.StatusUtil;
import ag.act.validator.document.DigitalProxyModuSignValidator;
import ag.act.validator.post.StockBoardGroupPostValidator;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
public class DigitalProxyModuSignFacade {

    private final ModuSignService moduSignService;
    private final StockBoardGroupPostValidator stockBoardGroupPostValidator;
    private final DigitalProxyModuSignValidator digitalProxyModuSignValidator;
    private final DigitalProxyApprovalService digitalProxyApprovalService;

    public DigitalProxyModuSignFacade(
        ModuSignService moduSignService,
        DigitalProxyApprovalService digitalProxyApprovalService,
        StockBoardGroupPostValidator stockBoardGroupPostValidator,
        DigitalProxyModuSignValidator digitalProxyModuSignValidator
    ) {
        this.moduSignService = moduSignService;
        this.stockBoardGroupPostValidator = stockBoardGroupPostValidator;
        this.digitalProxyModuSignValidator = digitalProxyModuSignValidator;
        this.digitalProxyApprovalService = digitalProxyApprovalService;
    }

    public ag.act.model.DigitalProxySignResponse signAndGetEmbeddedUrl(String stockCode, String boardGroupName, Long postId) {
        final User user = ActUserProvider.getNoneNull();
        final Post post = stockBoardGroupPostValidator.validateBoardGroupPost(postId, stockCode, boardGroupName, StatusUtil.getDeleteStatuses());
        final DigitalProxy digitalProxy = digitalProxyModuSignValidator.validateAndGet(post.getDigitalProxy());
        final Optional<DigitalProxyApproval> myDigitalProxyApproval
            = digitalProxyApprovalService.findMyDigitalProxyApproval(digitalProxy.getDigitalProxyApprovalList(), user.getId());

        final ModuSignDocument document = myDigitalProxyApproval
            .map(
                digitalProxyApproval ->
                    ModuSignDocument.builder()
                        .id(digitalProxyApproval.getDocumentId())
                        .participantId(digitalProxyApproval.getParticipantId())
                        .build()
            )
            .orElseGet(
                () -> {
                    digitalProxyModuSignValidator.validateBeforeApproval(digitalProxy);

                    return moduSignService.requestSignature(
                        digitalProxy.getTemplateId(),
                        digitalProxy.getTemplateName(),
                        digitalProxy.getTemplateRole()
                    );
                }
            );

        digitalProxyModuSignValidator.validateModuSignDocument(document);

        if (myDigitalProxyApproval.isEmpty()) {
            digitalProxyApprovalService.addDigitalProxyApproval(post, digitalProxy, user, document);
        }

        return new ag.act.model.DigitalProxySignResponse()
            .embeddedUrl(retrieveModuSignEmbeddedUrl(document.getId(), document.getParticipantId()));
    }

    private String retrieveModuSignEmbeddedUrl(String documentId, String participantId) {
        return moduSignService.getEmbeddedUrlInRetry(documentId, participantId);
    }
}
