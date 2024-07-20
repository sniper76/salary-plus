package ag.act.core.guard;

import ag.act.configuration.security.ActUserProvider;
import ag.act.exception.ActRuntimeException;
import ag.act.exception.BadRequestException;
import ag.act.service.user.UserRoleService;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class IsAdminGuard implements ActGuard {

    private final UserRoleService userRoleService;

    public IsAdminGuard(UserRoleService userRoleService) {
        this.userRoleService = userRoleService;
    }

    public void canActivate(Map<String, Object> parameterMap) throws ActRuntimeException {
        final Long userId = ActUserProvider.getNoneNull().getId();

        if (!userRoleService.isAdmin(userId)) {
            throw new BadRequestException("관리자만 가능한 기능입니다.");
        }
    }
}
