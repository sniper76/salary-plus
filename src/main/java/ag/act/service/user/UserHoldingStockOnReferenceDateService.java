package ag.act.service.user;

import ag.act.entity.UserHoldingStockOnReferenceDate;
import ag.act.repository.UserHoldingStockOnReferenceDateRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserHoldingStockOnReferenceDateService {
    private final UserHoldingStockOnReferenceDateRepository userHoldingStockOnReferenceDateRepository;

    public Optional<UserHoldingStockOnReferenceDate> getUserHoldingStockOnReferenceDate(
        Long userId,
        String stockCode,
        LocalDate stockReferenceDate
    ) {
        return userHoldingStockOnReferenceDateRepository.findByUserIdAndStockCodeAndReferenceDate(userId, stockCode, stockReferenceDate);
    }

    public long getQuantityUserHoldingStockOnReferenceDate(Long userId, String stockCode, LocalDate referenceDate) {
        return getUserHoldingStockOnReferenceDate(
                userId, stockCode, referenceDate
            )
            .map(UserHoldingStockOnReferenceDate::getQuantity)
            .orElse(0L);
    }

    public long countByPostUserViewDigitalDelegateForReferenceDate(Long userId) {
        return userHoldingStockOnReferenceDateRepository.countByPostUserViewDigitalDelegateForReferenceDate(userId);
    }

    public UserHoldingStockOnReferenceDate save(UserHoldingStockOnReferenceDate userHoldingStockOnReferenceDate) {
        return userHoldingStockOnReferenceDateRepository.save(userHoldingStockOnReferenceDate);
    }

    public void deleteAllByUserId(Long userId) {
        userHoldingStockOnReferenceDateRepository.deleteAllByUserId(userId);
    }

    public void deleteUserHoldingStockOnReferenceDate(Long userId, String stockCode) {
        userHoldingStockOnReferenceDateRepository.deleteAllByUserIdAndStockCode(userId, stockCode);
    }
}
