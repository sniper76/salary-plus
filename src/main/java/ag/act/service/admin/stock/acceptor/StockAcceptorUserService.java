package ag.act.service.admin.stock.acceptor;


import ag.act.entity.StockAcceptorUser;
import ag.act.exception.BadRequestException;
import ag.act.repository.admin.StockAcceptorUserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class StockAcceptorUserService {
    private final StockAcceptorUserRepository stockAcceptUserRepository;

    public Optional<StockAcceptorUser> findByStockCode(String stockCode) {
        return stockAcceptUserRepository.findByStockCode(stockCode);
    }

    public Optional<StockAcceptorUser> findByStockCodeAndUserId(String stockCode, Long userId) {
        return stockAcceptUserRepository.findByStockCodeAndUserId(stockCode, userId);
    }

    public StockAcceptorUser create(String stockCode, Long userId) {
        final StockAcceptorUser stockAcceptorUser = new StockAcceptorUser();
        stockAcceptorUser.setUserId(userId);
        stockAcceptorUser.setStockCode(stockCode);
        return save(stockAcceptorUser);
    }

    public StockAcceptorUser save(StockAcceptorUser stockAcceptorUser) {
        return stockAcceptUserRepository.save(stockAcceptorUser);
    }

    public void delete(String stockCode) {
        stockAcceptUserRepository.delete(
            getStockAcceptorUser(stockCode)
        );
    }

    public boolean hasRoleAndSingleStock(Long userId) {
        return stockAcceptUserRepository.findAllByUserId(userId).size() == 1;
    }

    private StockAcceptorUser getStockAcceptorUser(String stockCode) {
        return findByStockCode(stockCode)
            .orElseThrow(() -> new BadRequestException("종목에 해당하는 수임인 정보가 없습니다."));
    }
}
