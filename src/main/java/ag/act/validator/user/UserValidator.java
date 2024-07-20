package ag.act.validator.user;

import ag.act.core.configuration.GlobalBoardManager;
import ag.act.entity.User;
import ag.act.exception.DeletedUserException;
import ag.act.exception.NotHaveStockException;
import ag.act.util.StatusUtil;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class UserValidator {

    private final GlobalBoardManager globalBoardManager;

    public void validateStatus(@NotNull final User user) {
        if (StatusUtil.isDeletedOrWithdrawal(user.getStatus())) {
            throw new DeletedUserException("탈퇴한 회원입니다.");
        }
    }

    public void validateStatus(Optional<User> user) {
        user.ifPresent(this::validateStatus);
    }

    public void validateHavingStock(@NotNull final User user, String stockCode) {

        if (globalBoardManager.isGlobalStockCode(stockCode)) {
            return;
        }

        Optional.ofNullable(user.getUserHoldingStocks()).orElse(List.of())
            .stream()
            .filter(userHoldingStock -> userHoldingStock.getStockCode().equals(stockCode))
            .findFirst()
            .orElseThrow(() -> new NotHaveStockException("현재 주식을 보유하지 않은 회원입니다."));
    }
}
