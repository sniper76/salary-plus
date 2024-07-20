package ag.act.validator;

import ag.act.entity.StockReferenceDate;
import ag.act.entity.User;
import ag.act.exception.BadRequestException;
import ag.act.facade.user.UserFacade;
import ag.act.service.stock.StockServiceValidator;
import ag.act.service.user.UserHoldingStockService;
import ag.act.service.user.UserRoleService;
import ag.act.validator.user.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class AdminDummyStockValidator {

    private final UserFacade userFacade;
    private final UserValidator userValidator;
    private final StockServiceValidator stockServiceValidator;
    private final UserRoleService userRoleService;
    private final UserHoldingStockService userHoldingStockService;

    public void validateAdminActiveUser(Long userId) {
        final User user = userFacade.validateUserAndGet(userId);

        userValidator.validateStatus(user);

        if (!userRoleService.isAdmin(user.getId())) {
            throw new BadRequestException("해당 사용자는 어드민 권한이 없습니다.");
        }
    }

    public void validateStockAlreadyExists(Long userId, String stockCode) {
        stockServiceValidator.validateStockCode(stockCode);

        if (userHoldingStockService.hasUserHoldingStock(userId, stockCode)) {
            throw new BadRequestException("이미 해당 종목을 보유하고 있습니다.");
        }
    }

    public void validateStockReferenceDate(String stockCode, StockReferenceDate stockReferenceDate) {
        if (!Objects.equals(stockReferenceDate.getStockCode(), stockCode)) {
            throw new BadRequestException("해당 종목의 기준일 정보가 아닙니다.");
        }
    }

    public void validateDummyStock(Long userId, String stockCode) {
        if (!userHoldingStockService.hasDummyUserHoldingStock(userId, stockCode)) {
            throw new BadRequestException("해당 사용자가 해당 더미 종목을 가지고 있지 않습니다.");
        }
    }
}
