package ag.act.facade.admin.stock;

import ag.act.exception.BadRequestException;
import ag.act.model.CreatePrivateStockRequest;
import ag.act.service.stock.PrivateStockService;
import ag.act.service.stock.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminStockFacade {

    private final PrivateStockService privateStockService;
    private final StockService stockService;

    public void createPrivateStock(CreatePrivateStockRequest request) {
        validateCreate(request.getCode());
        privateStockService.save(request);
    }

    private void validateCreate(String code) {
        if (isAlreadyExist(code)) {
            throw new BadRequestException("Stock with code " + code + " already exists");
        }
    }

    private boolean isAlreadyExist(String code) {
        if (privateStockService.findByCode(code).isPresent()) {
            return true;
        }

        if (stockService.findByCode(code).isPresent()) {
            return true;
        }

        return false;
    }
}
