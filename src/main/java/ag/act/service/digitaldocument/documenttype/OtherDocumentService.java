package ag.act.service.digitaldocument.documenttype;

import ag.act.converter.digitaldocument.DigitalDocumentRequestCommonConverter;
import ag.act.converter.post.PostResponseDigitalDocumentConverter;
import ag.act.dto.download.DownloadFile;
import ag.act.entity.Post;
import ag.act.entity.User;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.entity.digitaldocument.IOtherDocument;
import ag.act.enums.DigitalDocumentType;
import ag.act.enums.digitaldocument.IdCardWatermarkType;
import ag.act.model.CreateDigitalDocumentRequest;
import ag.act.model.PreviewDigitalDocumentRequest;
import ag.act.repository.DigitalDocumentRepository;
import ag.act.service.digitaldocument.DigitalDocumentAnswerService;
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
public class OtherDocumentService implements DigitalDocumentTypeService<IOtherDocument> {
    private final DigitalDocumentRepository digitalDocumentRepository;
    private final DigitalDocumentRequestCommonConverter digitalDocumentRequestCommonConverter;
    private final PostResponseDigitalDocumentConverter postResponseDigitalDocumentConverter;
    private final DigitalDocumentAnswerService digitalDocumentAnswerService;
    private final DigitalDocumentPreviewService digitalDocumentPreviewService;
    private final StockService stockService;
    private final DigitalDocumentValidator digitalDocumentValidator;

    @Override
    public boolean supports(DigitalDocumentType type) {
        return type == DigitalDocumentType.ETC_DOCUMENT;
    }

    @Override
    public DigitalDocument create(Post post, CreateDigitalDocumentRequest createRequest) {
        digitalDocumentValidator.validateOtherDocument(createRequest);

        DigitalDocument digitalDocument = digitalDocumentRequestCommonConverter.convert(post, createRequest);
        digitalDocument.setTitle(createRequest.getTitle());
        digitalDocument.setContent(createRequest.getContent());
        digitalDocument.setIdCardWatermarkType(IdCardWatermarkType.fromValue(createRequest.getIdCardWatermarkType()));
        digitalDocument.setIsDisplayStockQuantity(createRequest.getIsDisplayStockQuantity());

        return digitalDocumentRepository.save(digitalDocument);
    }

    @Override
    public DigitalDocument update(IOtherDocument digitalDocument) {
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
            null,
            referenceDateStockCount,
            List.of()
        );
    }

    @Override
    public DownloadFile preview(PreviewDigitalDocumentRequest previewRequest) {
        digitalDocumentValidator.validateOtherDocument(previewRequest);

        return digitalDocumentPreviewService.createPreviewDocument(previewRequest);
    }
}
