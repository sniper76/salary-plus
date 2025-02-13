package ag.act.sp.service;

import ag.act.sp.entity.PricePlan;
import ag.act.sp.entity.Shop;
import ag.act.sp.entity.ShopGroup;
import ag.act.sp.repository.ShopRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Transactional
@Service
public class ShopService {
    private final ShopRepository shopRepository;

    public Optional<Shop> findById(Long id) {
        return shopRepository.findById(id);
    }

    public Shop createShop(Shop shop) {
        return shopRepository.save(shop);
    }
}
