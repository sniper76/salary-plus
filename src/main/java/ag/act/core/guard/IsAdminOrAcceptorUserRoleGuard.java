package ag.act.core.guard;

import ag.act.configuration.security.ActUserProvider;
import ag.act.entity.User;
import ag.act.exception.ActRuntimeException;
import ag.act.exception.BadRequestException;
import ag.act.service.user.UserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class IsAdminOrAcceptorUserRoleGuard implements ActGuard {

    private final UserRoleService userRoleService;

    public void canActivate(Map<String, Object> parameterMap) throws ActRuntimeException {
        final User user = ActUserProvider.getNoneNull();

        if (!isAdmin(user) && !isAcceptorUser(user)) {
            throw new BadRequestException("권한이 부족합니다.");
        }
    }

    private boolean isAdmin(User user) {
        return user.isAdmin() || userRoleService.isAdmin(user.getId());
    }

    private boolean isAcceptorUser(User user) {
        return user.isAcceptor() || userRoleService.isAcceptorUser(user.getId());
    }
}
