package ag.act.converter;

import ag.act.configuration.security.ActUserProvider;
import ag.act.converter.stock.StockResponseConverter;
import ag.act.entity.Post;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.entity.digitaldocument.DigitalDocumentUser;
import ag.act.model.PostDigitalDocumentResponse;
import ag.act.model.UserDigitalDocumentResponse;
import ag.act.service.digitaldocument.DigitalDocumentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PostDigitalDocumentResponseConverter {
    private final StockResponseConverter stockResponseConverter;
    private final DigitalDocumentUserService digitalDocumentUserService;

    public PostDigitalDocumentResponse convert(Post post) {
        final Stock stock = post.getBoard().getStock();
        final DigitalDocument digitalDocument = post.getDigitalDocument();
        final User user = ActUserProvider.getNoneNull();

        return new PostDigitalDocumentResponse()
            .id(post.getId())
            .title(post.getTitle())
            .content(post.getContent())
            .status(post.getStatus())
            .stock(stockResponseConverter.convert(stock))
            .digitalDocument(new UserDigitalDocumentResponse()
                .digitalDocumentType(digitalDocument.getType().name())
                .joinStockSum(digitalDocument.getJoinStockSum())
                .joinUserCount(digitalDocument.getJoinUserCount())
                .shareholdingRatio(getShareholdingRatio(digitalDocument))
                .answerStatus(getAnswerStatus(digitalDocument.getId(), user.getId()))
                .targetEndDate(DateTimeConverter.convert(digitalDocument.getTargetEndDate()))
                .targetStartDate(DateTimeConverter.convert(digitalDocument.getTargetStartDate()))
            )
            .createdAt(DateTimeConverter.convert(post.getCreatedAt()))
            .updatedAt(DateTimeConverter.convert(post.getUpdatedAt()))
            ;
    }

    private float getShareholdingRatio(DigitalDocument digitalDocument) {
        return Optional.ofNullable(digitalDocument.getShareholdingRatio()).map(Double::floatValue).orElse(0f);
    }

    private String getAnswerStatus(Long digitalDocumentId, Long userId) {
        final Optional<DigitalDocumentUser> documentUserOptional = digitalDocumentUserService.findByDigitalDocumentIdAndUserId(
            digitalDocumentId, userId
        );

        return documentUserOptional
            .map(digitalDocumentUser -> digitalDocumentUser.getDigitalDocumentAnswerStatus().name())
            .orElse(null);
    }
}
