package ag.act.service.admin.stock.acceptor;


import ag.act.converter.digitaldocument.StockAcceptorUserHistoryConverter;
import ag.act.entity.StockAcceptorUserHistory;
import ag.act.entity.User;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.exception.BadRequestException;
import ag.act.model.Status;
import ag.act.repository.admin.StockAcceptorUserHistoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class StockAcceptorUserHistoryService {
    private final StockAcceptorUserHistoryRepository stockAcceptorUserHistoryRepository;
    private final StockAcceptorUserHistoryConverter stockAcceptorUserHistoryConverter;
    private final PasswordEncoder passwordEncoder;

    public StockAcceptorUserHistory create(String stockCode, User user, Status status) {
        final StockAcceptorUserHistory stockAcceptorUserHistory = new StockAcceptorUserHistory();
        stockAcceptorUserHistory.setUserId(user.getId());
        stockAcceptorUserHistory.setStockCode(stockCode);
        stockAcceptorUserHistory.setStatus(status);
        stockAcceptorUserHistory.setName(user.getName());
        stockAcceptorUserHistory.setBirthDate(user.getBirthDate());
        stockAcceptorUserHistory.setHashedPhoneNumber(user.getHashedPhoneNumber());
        stockAcceptorUserHistory.setFirstNumberOfIdentification(user.getFirstNumberOfIdentification());
        return save(stockAcceptorUserHistory);
    }

    public User getUserByStockAcceptorUserHistory(String stockCode, Long acceptUserId) {
        return stockAcceptorUserHistoryConverter.convertToUser(
            getAcceptorUserHistory(stockCode, acceptUserId)
        );
    }

    public StockAcceptorUserHistory save(StockAcceptorUserHistory stockAcceptorUserHistory) {
        return stockAcceptorUserHistoryRepository.save(stockAcceptorUserHistory);
    }

    public void replaceUserInfoWithDummy(DigitalDocument digitalDocument) {
        final Optional<StockAcceptorUserHistory> optionalStockAcceptorUserHistory = findStockAcceptorUserHistory(
            digitalDocument.getStockCode(), digitalDocument.getAcceptUserId()
        );

        optionalStockAcceptorUserHistory.ifPresentOrElse(
            stockAcceptorUserHistory -> {
                stockAcceptorUserHistory.setName("컨두잇");
                stockAcceptorUserHistory.setHashedPhoneNumber(passwordEncoder.encode(stockAcceptorUserHistory.getUserId().toString()));
                stockAcceptorUserHistory.setBirthDate(LocalDateTime.now());
                save(stockAcceptorUserHistory);
            },
            () -> log.warn(
                "90일 지난 전자문서 삭제시 수임인 정보 미존재. stockCode: {}, acceptUserId: {}",
                digitalDocument.getStockCode(),
                digitalDocument.getAcceptUserId()
            )
        );
    }

    private StockAcceptorUserHistory getAcceptorUserHistory(String stockCode, Long acceptUserId) {
        return findStockAcceptorUserHistory(stockCode, acceptUserId)
            .orElseThrow(() -> new BadRequestException("전자문서 수임인 히스토리 정보가 없습니다."));
    }

    private Optional<StockAcceptorUserHistory> findStockAcceptorUserHistory(String stockCode, Long userId) {
        return stockAcceptorUserHistoryRepository.findFirstByStockCodeAndUserIdOrderByCreatedAtDesc(stockCode, userId);
    }
}
