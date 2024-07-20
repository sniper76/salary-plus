package ag.act.validator.user;

import ag.act.entity.User;
import ag.act.enums.AppPreferenceType;
import ag.act.exception.BadRequestException;
import ag.act.module.cache.AppPreferenceCache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class BlockedUserValidator {

    private final AppPreferenceCache appPreferenceCache;

    public void validateTargetUser(User targetUser) {
        List<Long> exceptedUserIds = appPreferenceCache.getValue(AppPreferenceType.BLOCK_EXCEPT_USER_IDS);

        if (exceptedUserIds.contains(targetUser.getId())) {
            throw new BadRequestException("해당 계정은 운영계정으로 차단이 불가합니다.");
        }
    }
}
