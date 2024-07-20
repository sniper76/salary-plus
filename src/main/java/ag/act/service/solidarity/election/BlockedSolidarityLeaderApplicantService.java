package ag.act.service.solidarity.election;

import ag.act.entity.Stock;
import ag.act.entity.solidarity.election.BlockedSolidarityLeaderApplicant;
import ag.act.exception.BadRequestException;
import ag.act.repository.solidarity.election.BlockedSolidarityLeaderApplicantRepository;
import ag.act.service.stock.StockService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BlockedSolidarityLeaderApplicantService {

    private final StockService stockService;
    private final BlockedSolidarityLeaderApplicantRepository blockedSolidarityLeaderApplicantRepository;

    public void createBlockedSolidarityLeaderApplicant(Long userId, String stockCode, String reasons) {
        Stock stock = stockService.getStock(stockCode);

        BlockedSolidarityLeaderApplicant blockedSolidarityLeaderApplicant = new BlockedSolidarityLeaderApplicant();
        blockedSolidarityLeaderApplicant.setSolidarityId(stock.getSolidarityId());
        blockedSolidarityLeaderApplicant.setUserId(userId);
        blockedSolidarityLeaderApplicant.setReasons(reasons);
        blockedSolidarityLeaderApplicant.setStockCode(stock.getCode());

        try {
            blockedSolidarityLeaderApplicantRepository.save(blockedSolidarityLeaderApplicant);
        } catch (DataIntegrityViolationException exception) {
            log.warn("{} 종목의 지원자 중 지원자의 유저 아이디 {}을 차단하는 과정 중에 문제가 발생하였습니다.", stock.getCode(), userId, exception);
            throw new BadRequestException("이미 지원이 차단된 사용자입니다.");
        }
    }

    public Optional<BlockedSolidarityLeaderApplicant> findByStockCodeAndUserId(String stockCode, Long userId) {
        return blockedSolidarityLeaderApplicantRepository.findByStockCodeAndUserId(stockCode, userId);
    }
}
