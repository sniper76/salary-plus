package ag.act.core.guard;


import ag.act.configuration.security.ActUserProvider;
import ag.act.core.guard.holdingstock.UserHoldingStockAndDigitalDocumentValidator;
import ag.act.entity.ActUser;
import ag.act.entity.Post;
import ag.act.entity.User;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.enums.DigitalDocumentType;
import ag.act.exception.ActRuntimeException;
import ag.act.exception.UserNotHaveStockException;
import ag.act.util.StatusUtil;
import ag.act.validator.DefaultObjectValidator;
import ag.act.validator.post.StockBoardGroupPostValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class PostDetailGuard implements ActGuard {
    private final StockBoardGroupPostValidator stockBoardGroupPostValidator;
    private final UserHoldingStockAndDigitalDocumentValidator userHoldingStockAndDigitalDocumentValidator;
    private final DefaultObjectValidator defaultObjectValidator;

    public void canActivate(Map<String, Object> parameterMap) throws ActRuntimeException {
        final String stockCode = (String) parameterMap.get("stockCode");
        final String boardGroupName = (String) parameterMap.get("boardGroupName");
        final Long postId = (Long) parameterMap.get("postId");
        final Post post = stockBoardGroupPostValidator.validateBoardGroupPost(postId, stockCode, boardGroupName);

        Optional<User> user = ActUserProvider.get();
        boolean isAdmin = user.map(ActUser::isAdmin).orElse(false);
        if (!isAdmin) {
            defaultObjectValidator.validateStatus(post, StatusUtil.getDeletedStatusesForPostDetails(), "이미 삭제된 게시글입니다.");
        }

        boolean isGuest = user.isEmpty();
        if (isGuest) {
            return;
        }

        final DigitalDocument digitalDocument = post.getDigitalDocument();
        if (digitalDocument == null) {
            return;
        }
        if (isDigitalDocument(digitalDocument)) {
            if (!hasUserHoldingStockOnReferenceDate(user.get(), digitalDocument)) {
                throw new UserNotHaveStockException("보유하고 있지 않은 주식입니다.");
            }
        } else {
            userHoldingStockAndDigitalDocumentValidator.validateUserHoldingStock(user.get(), stockCode);
        }
    }

    private boolean hasUserHoldingStockOnReferenceDate(User user, DigitalDocument digitalDocument) {
        return userHoldingStockAndDigitalDocumentValidator.validateUserHoldingStockOnReferenceDate(user, digitalDocument);
    }

    private boolean isDigitalDocument(DigitalDocument digitalDocument) {
        return digitalDocument.getType() == DigitalDocumentType.DIGITAL_PROXY;
    }
}
