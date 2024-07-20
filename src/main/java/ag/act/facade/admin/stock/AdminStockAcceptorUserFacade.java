package ag.act.facade.admin.stock;

import ag.act.entity.User;
import ag.act.facade.user.UserRoleFacade;
import ag.act.model.Status;
import ag.act.service.admin.stock.acceptor.StockAcceptorUserHistoryService;
import ag.act.service.admin.stock.acceptor.StockAcceptorUserService;
import ag.act.validator.user.AdminStockAcceptorUserValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Transactional
public class AdminStockAcceptorUserFacade {
    private final UserRoleFacade userRoleFacade;
    private final StockAcceptorUserService stockAcceptorUserService;
    private final StockAcceptorUserHistoryService stockAcceptorUserHistoryService;
    private final AdminStockAcceptorUserValidator adminStockAcceptorUserValidator;

    public void createStockAcceptorUser(String stockCode, Long userId) {
        final User user = adminStockAcceptorUserValidator.validate(stockCode, userId);
        stockAcceptorUserService.create(stockCode, user.getId());
        stockAcceptorUserHistoryService.create(stockCode, user, Status.ACTIVE);
        userRoleFacade.assignAcceptorRoleToUser(userId);
    }

    public void deleteStockAcceptorUser(String stockCode, Long userId) {
        final User user = adminStockAcceptorUserValidator.validateForDelete(stockCode, userId);
        if (stockAcceptorUserService.hasRoleAndSingleStock(userId)) {
            userRoleFacade.revokeAcceptorToUser(userId);
        }
        stockAcceptorUserService.delete(stockCode);
        stockAcceptorUserHistoryService.create(stockCode, user, Status.DELETED_BY_ADMIN);
    }
}
