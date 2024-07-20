package ag.act.core.guard;

import ag.act.configuration.security.ActUserProvider;
import ag.act.entity.User;
import ag.act.exception.ActRuntimeException;
import ag.act.exception.ForbiddenException;
import ag.act.util.DateTimeUtil;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PinNumberVerificationGuard implements ActGuard {

    public void canActivate(Map<String, Object> parameterMap) throws ActRuntimeException {
        verifyPinNumber(ActUserProvider.getNoneNull());
    }

    private void verifyPinNumber(User user) {
        if (user.getHashedPinNumber() == null) {
            throw new ForbiddenException("핀번호가 설정되지 않았습니다.");
        }

        if (user.getLastPinNumberVerifiedAt() == null) {
            throw new ForbiddenException("핀번호 검증에 실패했습니다.");
        }

        if (DateTimeUtil.isBeforeToday(user.getLastPinNumberVerifiedAt())) {
            throw new ForbiddenException("핀번호 검증토큰이 만료되었습니다.");
        }
    }
}
