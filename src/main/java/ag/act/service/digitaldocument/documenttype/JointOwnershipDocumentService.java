package ag.act.service.digitaldocument.documenttype;

import ag.act.converter.digitaldocument.DigitalDocumentRequestCommonConverter;
import ag.act.converter.post.PostResponseDigitalDocumentConverter;
import ag.act.dto.download.DownloadFile;
import ag.act.entity.Post;
import ag.act.entity.User;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.entity.digitaldocument.IJointOwnershipDocument;
import ag.act.enums.DigitalDocumentType;
import ag.act.enums.digitaldocument.IdCardWatermarkType;
import ag.act.exception.BadRequestException;
import ag.act.model.CreateDigitalDocumentRequest;
import ag.act.model.PreviewDigitalDocumentRequest;
import ag.act.repository.DigitalDocumentRepository;
import ag.act.service.digitaldocument.DigitalDocumentAcceptUserService;
import ag.act.service.digitaldocument.DigitalDocumentAnswerService;
import ag.act.service.digitaldocument.DigitalDocumentPreProcessor;
import ag.act.service.digitaldocument.DigitalDocumentPreviewService;
import ag.act.service.stock.StockService;
import ag.act.validator.document.DigitalDocumentValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class JointOwnershipDocumentService implements DigitalDocumentTypeService<IJointOwnershipDocument> {
    private final DigitalDocumentRepository digitalDocumentRepository;
    private final DigitalDocumentRequestCommonConverter digitalDocumentRequestCommonConverter;
    private final DigitalDocumentAcceptUserService digitalDocumentAcceptUserService;
    private final PostResponseDigitalDocumentConverter postResponseDigitalDocumentConverter;
    private final StockService stockService;
    private final DigitalDocumentAnswerService digitalDocumentAnswerService;
    private final DigitalDocumentPreProcessor digitalDocumentPreProcessor;
    private final DigitalDocumentValidator digitalDocumentValidator;
    private final DigitalDocumentPreviewService digitalDocumentPreviewService;

    @Override
    public boolean supports(DigitalDocumentType type) {
        return type == DigitalDocumentType.JOINT_OWNERSHIP_DOCUMENT;
    }

    @Override
    public DigitalDocument create(Post post, CreateDigitalDocumentRequest createRequest) {
        digitalDocumentPreProcessor.validateJointOwnershipDocumentForCreate(createRequest, post.getBoard().getStockCode());

        DigitalDocument digitalDocument = digitalDocumentRequestCommonConverter.convert(post, createRequest);
        digitalDocument.setTitle(digitalDocument.getType().getDisplayName());
        digitalDocument.setContent(createRequest.getContent());
        digitalDocument.setCompanyRegistrationNumber(createRequest.getCompanyRegistrationNumber());
        digitalDocument.setIdCardWatermarkType(IdCardWatermarkType.fromValue(createRequest.getIdCardWatermarkType()));

        return digitalDocumentRepository.save(digitalDocument);
    }

    @Override
    public DigitalDocument update(IJointOwnershipDocument digitalDocument) {
        return null;
    }

    @Override
    public ag.act.model.UserDigitalDocumentResponse getPostResponseDigitalDocument(
        Post post, User currentUser, Long referenceDateStockCount
    ) {
        DigitalDocument digitalDocument = post.getDigitalDocument();

        return postResponseDigitalDocumentConverter.convert(
            digitalDocument,
            digitalDocumentAnswerService.getDigitalDocumentAnswerStatus(currentUser, digitalDocument),
            stockService.getStock(post.getBoard().getStockCode()),
            currentUser,
            digitalDocumentAcceptUserService.getAcceptUserForPostDetail(digitalDocument),
            referenceDateStockCount,
            List.of()
        );
    }

    @Override
    public DownloadFile preview(PreviewDigitalDocumentRequest previewRequest) {
        digitalDocumentValidator.validateJointOwnershipDocument(previewRequest);
        if (previewRequest.getAcceptUserId() == null) {
            throw new BadRequestException("수임인 ID를 확인하세요.");
        }

        return digitalDocumentPreviewService.createPreviewDocument(previewRequest);
    }
}
