package ag.act.service.digitaldocument.documenttype;

import ag.act.converter.DateTimeConverter;
import ag.act.converter.digitaldocument.DigitalDocumentRequestCommonConverter;
import ag.act.converter.post.PostResponseDigitalDocumentConverter;
import ag.act.dto.download.DownloadFile;
import ag.act.entity.Post;
import ag.act.entity.User;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.entity.digitaldocument.IDigitalProxy;
import ag.act.enums.DigitalDocumentType;
import ag.act.enums.digitaldocument.IdCardWatermarkType;
import ag.act.exception.BadRequestException;
import ag.act.model.CreateDigitalDocumentRequest;
import ag.act.model.PreviewDigitalDocumentRequest;
import ag.act.repository.DigitalDocumentRepository;
import ag.act.service.digitaldocument.DigitalDocumentAcceptUserService;
import ag.act.service.digitaldocument.DigitalDocumentAnswerService;
import ag.act.service.digitaldocument.DigitalDocumentItemService;
import ag.act.service.digitaldocument.DigitalDocumentPreProcessor;
import ag.act.service.digitaldocument.DigitalDocumentPreviewService;
import ag.act.service.stock.StockService;
import ag.act.util.DigitalDocumentItemTreeGenerator;
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
public class DigitalProxyService implements DigitalDocumentTypeService<IDigitalProxy> {
    private final DigitalDocumentRequestCommonConverter digitalDocumentRequestCommonConverter;
    private final DigitalDocumentRepository digitalDocumentRepository;
    private final DigitalDocumentItemService digitalDocumentItemService;
    private final DigitalDocumentAcceptUserService digitalDocumentAcceptUserService;
    private final PostResponseDigitalDocumentConverter postResponseDigitalDocumentConverter;
    private final StockService stockService;
    private final DigitalDocumentItemTreeGenerator digitalDocumentItemTreeGenerator;
    private final DigitalDocumentAnswerService digitalDocumentAnswerService;
    private final DigitalDocumentPreProcessor digitalDocumentPreProcessor;
    private final DigitalDocumentValidator digitalDocumentValidator;
    private final DigitalDocumentPreviewService digitalDocumentPreviewService;

    @Override
    public boolean supports(DigitalDocumentType type) {
        return type == DigitalDocumentType.DIGITAL_PROXY;
    }

    @Override
    public DigitalDocument create(Post post, CreateDigitalDocumentRequest createRequest) {
        digitalDocumentPreProcessor.validateDigitalDocumentForCreate(createRequest, post.getBoard().getStockCode());

        DigitalDocument digitalDocument = digitalDocumentRequestCommonConverter.convert(post, createRequest);
        digitalDocument.setTitle(digitalDocument.getType().getDisplayName());
        digitalDocument.setShareholderMeetingDate(DateTimeConverter.convert(createRequest.getShareholderMeetingDate()));
        digitalDocument.setShareholderMeetingName(createRequest.getShareholderMeetingName());
        digitalDocument.setShareholderMeetingType(createRequest.getShareholderMeetingType());
        digitalDocument.setDesignatedAgentNames(createRequest.getDesignatedAgentNames());
        digitalDocument.setIdCardWatermarkType(IdCardWatermarkType.fromValue(createRequest.getIdCardWatermarkType()));

        DigitalDocument savedDigitalDocument = digitalDocumentRepository.save(digitalDocument);

        digitalDocumentItemService.createItems(createRequest.getChildItems(), savedDigitalDocument.getId());
        savedDigitalDocument.setDigitalDocumentItemList(
            digitalDocumentItemService.findDigitalDocumentItemsByDigitalDocumentId(savedDigitalDocument.getId())
        );

        return savedDigitalDocument;
    }

    @Override
    public DigitalDocument update(IDigitalProxy digitalDocument) {
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
            getDigitalDocumentItemResponses(digitalDocument)
        );
    }

    private List<ag.act.model.DigitalDocumentItemResponse> getDigitalDocumentItemResponses(DigitalDocument digitalDocument) {
        return digitalDocumentItemTreeGenerator.buildTree(digitalDocument.getDigitalDocumentItemList());
    }

    @Override
    public DownloadFile preview(PreviewDigitalDocumentRequest previewRequest) {
        digitalDocumentValidator.validateDigitalProxy(previewRequest);
        if (previewRequest.getAcceptUserId() == null) {
            throw new BadRequestException("수임인 ID를 확인하세요.");
        }

        return digitalDocumentPreviewService.createPreviewDocument(previewRequest);
    }
}
