package ag.act.sp.service;

import ag.act.exception.BadRequestException;
import ag.act.sp.entity.PricePlan;
import ag.act.sp.entity.Shop;
import ag.act.sp.repository.PricePlanRepository;
import ag.act.sp.repository.ShopRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Transactional
@Service
public class PricePlanService {
    private final PricePlanRepository pricePlanRepository;

    public PricePlan getPricePlan(Long id) {
        return pricePlanRepository.findById(id)
            .orElseThrow(() -> new BadRequestException("요금제 정보가 존재하지 않습니다."));
    }
}
