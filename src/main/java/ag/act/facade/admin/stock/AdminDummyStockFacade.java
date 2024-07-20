package ag.act.facade.admin.stock;

import ag.act.converter.admin.UserDummyStockResponseConverter;
import ag.act.dto.SimplePageDto;
import ag.act.dto.admin.UserDummyStockDto;
import ag.act.entity.StockReferenceDate;
import ag.act.entity.UserHoldingStock;
import ag.act.entity.UserHoldingStockOnReferenceDate;
import ag.act.model.AddDummyStockToUserRequest;
import ag.act.model.DeleteDummyStockFromUserRequest;
import ag.act.model.Status;
import ag.act.model.UserDummyStockResponse;
import ag.act.service.stock.StockReferenceDateService;
import ag.act.service.user.UserHoldingStockOnReferenceDateService;
import ag.act.service.user.UserHoldingStockService;
import ag.act.validator.AdminDummyStockValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AdminDummyStockFacade {

    private static final long DEFAULT_QUANTITY = 500L;
    private final UserHoldingStockService userHoldingStockService;
    private final UserHoldingStockOnReferenceDateService userHoldingStockOnReferenceDateService;
    private final StockReferenceDateService stockReferenceDateService;
    private final AdminDummyStockValidator adminDummyStockValidator;
    private final UserDummyStockResponseConverter userDummyStockResponseConverter;

    public SimplePageDto<UserDummyStockResponse> getUserDummyStocks(Long userId, Pageable pageable) {
        Page<UserDummyStockDto> dummyStockPage =
                userHoldingStockService.getUserDummyStocks(userId, pageable);
        return new SimplePageDto<>(dummyStockPage.map(userDummyStockResponseConverter::convert));
    }

    public void addDummyStockToUser(Long userId, AddDummyStockToUserRequest addDummyStockToUserRequest) {
        final String stockCode = addDummyStockToUserRequest.getStockCode();
        final StockReferenceDate stockReferenceDate = getStockReferenceDate(stockCode, addDummyStockToUserRequest.getStockReferenceDateId());

        adminDummyStockValidator.validateAdminActiveUser(userId);
        adminDummyStockValidator.validateStockAlreadyExists(userId, stockCode);

        userHoldingStockService.save(new UserHoldingStock(userId, stockCode, DEFAULT_QUANTITY));
        userHoldingStockOnReferenceDateService.save(
                new UserHoldingStockOnReferenceDate(
                        userId,
                        stockCode,
                        DEFAULT_QUANTITY,
                        stockReferenceDate.getReferenceDate(),
                        Status.ACTIVE
                )
        );
    }

    public void deleteDummyStockToUser(Long userId, DeleteDummyStockFromUserRequest deleteDummyStockFromUserRequest) {
        final String stockCode = deleteDummyStockFromUserRequest.getStockCode();

        adminDummyStockValidator.validateAdminActiveUser(userId);
        adminDummyStockValidator.validateDummyStock(userId, stockCode);

        userHoldingStockService.deleteDummyUserHoldingStock(userId, stockCode);
        userHoldingStockOnReferenceDateService.deleteUserHoldingStockOnReferenceDate(userId, stockCode);
    }

    private StockReferenceDate getStockReferenceDate(String stockCode, Long stockReferenceDateId) {
        final StockReferenceDate stockReferenceDate = stockReferenceDateService.getStockReferenceDate(stockReferenceDateId);

        adminDummyStockValidator.validateStockReferenceDate(stockCode, stockReferenceDate);

        return stockReferenceDate;
    }
}
