package ag.act.sp.handler;

import ag.act.api.CommonApiDelegate;
import ag.act.api.ShopApiDelegate;
import ag.act.facade.stock.StockFacade;
import ag.act.model.GetSimpleStockDataResponse;
import ag.act.model.SimpleStringResponse;
import ag.act.sp.facade.ShopFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class ShopApiDelegateImpl implements ShopApiDelegate {
    private final ShopFacade shopFacade;

    @Override
    public ResponseEntity<SimpleStringResponse> createShop(
        String shopName, Long pricePlanId, String userShiftStartTime, Long shopGroupId, MultipartFile userListFile
    ) {
        return ResponseEntity.ok(shopFacade.createShop(shopName, pricePlanId, userShiftStartTime, shopGroupId, userListFile));
    }
}
