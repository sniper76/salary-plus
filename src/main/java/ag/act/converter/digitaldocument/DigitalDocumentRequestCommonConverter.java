package ag.act.converter.digitaldocument;

import ag.act.converter.DateTimeConverter;
import ag.act.entity.Post;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.enums.DigitalDocumentType;
import ag.act.enums.DigitalDocumentVersion;
import ag.act.service.stock.StockReferenceDateService;
import org.springframework.stereotype.Component;

@Component
public class DigitalDocumentRequestCommonConverter {
    private final StockReferenceDateService stockReferenceDateService;

    public DigitalDocumentRequestCommonConverter(
        StockReferenceDateService stockReferenceDateService
    ) {
        this.stockReferenceDateService = stockReferenceDateService;
    }

    public DigitalDocument convert(Post post, ag.act.model.CreateDigitalDocumentRequest createRequest) {
        DigitalDocument digitalDocument = new DigitalDocument();
        digitalDocument.setVersion(DigitalDocumentVersion.fromValue(createRequest.getVersion()));
        digitalDocument.setType(DigitalDocumentType.fromValue(createRequest.getType()));
        digitalDocument.setStockReferenceDate(
            stockReferenceDateService.findReferenceDate(
                createRequest.getType(), post.getBoard().getStockCode(), createRequest.getStockReferenceDateId()
            )
        );
        digitalDocument.setPostId(post.getId());
        digitalDocument.setStockCode(post.getBoard().getStockCode());
        digitalDocument.setCompanyName(createRequest.getCompanyName());
        digitalDocument.setAcceptUserId(createRequest.getAcceptUserId());
        digitalDocument.setTargetStartDate(DateTimeConverter.convert(createRequest.getTargetStartDate()));
        digitalDocument.setTargetEndDate(DateTimeConverter.convert(createRequest.getTargetEndDate()));
        digitalDocument.setStatus(ag.act.model.Status.ACTIVE);
        digitalDocument.setJsonAttachOption(createRequest.getAttachOptions());
        digitalDocument.setIsDisplayStockQuantity(createRequest.getIsDisplayStockQuantity());

        post.setDigitalDocument(digitalDocument);
        digitalDocument.setPost(post);

        return digitalDocument;
    }
}
