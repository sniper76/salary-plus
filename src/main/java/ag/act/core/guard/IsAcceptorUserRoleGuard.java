package ag.act.core.guard;

import ag.act.configuration.security.ActUserProvider;
import ag.act.exception.ActRuntimeException;
import ag.act.exception.BadRequestException;
import ag.act.service.user.UserRoleService;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class IsAcceptorUserRoleGuard implements ActGuard {

    private final UserRoleService userRoleService;

    public IsAcceptorUserRoleGuard(UserRoleService userRoleService) {
        this.userRoleService = userRoleService;
    }

    public void canActivate(Map<String, Object> parameterMap) throws ActRuntimeException {
        final Long userId = ActUserProvider.getNoneNull().getId();

        if (!userRoleService.isAcceptorUser(userId)) {
            throw new BadRequestException("수임인만 사용 가능한 기능입니다.");
        }
    }
}
