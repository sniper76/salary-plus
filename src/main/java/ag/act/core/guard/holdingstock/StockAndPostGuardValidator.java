package ag.act.core.guard.holdingstock;

import ag.act.configuration.security.ActUserProvider;
import ag.act.entity.Post;
import ag.act.entity.User;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.enums.DigitalDocumentType;
import ag.act.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Order(40)
@RequiredArgsConstructor
@Component
public class StockAndPostGuardValidator implements HoldingStockGuardValidator {
    private final PostService postService;
    private final UserHoldingStockAndDigitalDocumentValidator userHoldingStockAndDigitalDocumentValidator;

    @Override
    public boolean validate(HoldingStockGuardParameter parameter) {
        final String stockCode = parameter.stockCode();
        final Long postId = parameter.postId();
        final User user = ActUserProvider.getNoneNull();

        if (stockCode == null || postId == null) {
            return false;
        }

        final Optional<Post> post = postService.findById(postId);
        if (post.isEmpty()) {
            return userHoldingStockAndDigitalDocumentValidator.validateUserHoldingStock(user, stockCode);
        }

        final DigitalDocument digitalDocument = post.get().getDigitalDocument();
        if (isDigitalProxy(digitalDocument)) {
            return userHoldingStockAndDigitalDocumentValidator.validateUserHoldingStockByDigitalDocument(user, digitalDocument);
        }

        return false;
    }

    private boolean isDigitalProxy(DigitalDocument digitalDocument) {
        return digitalDocument != null && digitalDocument.getType() == DigitalDocumentType.DIGITAL_PROXY;
    }
}
