package ag.act.sp.facade;

import ag.act.entity.User;
import ag.act.model.SimpleStringResponse;
import ag.act.sp.entity.PricePlan;
import ag.act.sp.entity.Shop;
import ag.act.sp.entity.ShopGroup;
import ag.act.sp.service.CsvService;
import ag.act.sp.service.PricePlanService;
import ag.act.sp.service.ShopGroupService;
import ag.act.sp.service.ShopService;
import ag.act.util.SimpleStringResponseUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalTime;
import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class ShopFacade {
    private final PricePlanService pricePlanService;
    private final ShopGroupService shopGroupService;
    private final ShopService shopService;
    private final CsvService csvService;

    public SimpleStringResponse createShop(
        String shopName, Long pricePlanId, String userShiftStartTime, Long shopGroupId, MultipartFile userListFile
    ) {
        final PricePlan pricePlan = pricePlanService.getPricePlan(pricePlanId);
        if (shopGroupId != null) {
            final ShopGroup shopGroup = shopGroupService.getShopGroup(shopGroupId);
        }
        if (userListFile != null) {
            // read csv file
            final List<User> userList = csvService.getUsersFromCsv(userListFile);
        }
        shopService.createShop(Shop.builder()
            .name(shopName)
            .userShiftStartTime(LocalTime.parse(userShiftStartTime))
            .pricePlanId(pricePlanId)
            .shopGroupId(shopGroupId)
            .build());
        return SimpleStringResponseUtil.ok();
    }
}
