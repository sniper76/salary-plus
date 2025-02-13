package ag.act.sp.service;

import ag.act.exception.BadRequestException;
import ag.act.sp.entity.PricePlan;
import ag.act.sp.entity.ShopGroup;
import ag.act.sp.repository.PricePlanRepository;
import ag.act.sp.repository.ShopGroupRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Transactional
@Service
public class ShopGroupService {
    private final ShopGroupRepository shopGroupRepository;

    public ShopGroup getShopGroup(Long id) {
        return shopGroupRepository.findById(id)
            .orElseThrow(() -> new BadRequestException("상점 그룹 정보가 존재하지 않습니다."));
    }
}
