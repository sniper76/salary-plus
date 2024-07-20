package ag.act.core.guard.holdingstock;

import ag.act.entity.User;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.exception.UserNotHaveStockException;
import ag.act.service.digitaldocument.DigitalDocumentService;
import ag.act.service.user.UserHoldingStockOnReferenceDateService;
import ag.act.service.user.UserHoldingStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserHoldingStockAndDigitalDocumentValidator {
    private final DigitalDocumentService digitalDocumentService;
    private final UserHoldingStockService userHoldingStockService;
    private final UserHoldingStockOnReferenceDateService userHoldingStockOnReferenceDateService;

    public boolean validateUserHoldingStockByDigitalDocument(User user, Long digitalDocumentId) {
        return digitalDocumentService.findDigitalDocument(digitalDocumentId)
            .filter(document -> validateUserHoldingStockByDigitalDocument(user, document))
            .isPresent();
    }

    public boolean validateUserHoldingStockByDigitalDocument(User user, DigitalDocument digitalDocument) {
        return validateUserHoldingStockOnReferenceDate(user, digitalDocument)
            || validateUserHoldingStock(user, digitalDocument.getStockCode());
    }

    public boolean validateUserHoldingStockOnReferenceDate(User user, DigitalDocument digitalDocument) {
        var userHoldingStockOnReferenceDate = userHoldingStockOnReferenceDateService.getUserHoldingStockOnReferenceDate(
            user.getId(),
            digitalDocument.getStockCode(),
            digitalDocument.getStockReferenceDate()
        );

        return userHoldingStockOnReferenceDate.isPresent();
    }

    public boolean validateUserHoldingStock(User user, String stockCode) {
        var userHoldingStock = userHoldingStockService.findUserHoldingStock(user.getId(), stockCode);
        if (userHoldingStock.isEmpty()) {
            throw new UserNotHaveStockException("보유하고 있지 않은 주식입니다.");
        }
        return true;
    }
}
