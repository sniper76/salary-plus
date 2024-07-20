package ag.act.service.user;

import ag.act.configuration.security.ActUserProvider;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AdminUserService {
    public List<Long> getSuperAdminUserIds() {
        return List.of(ActUserProvider.getSuperAdminUserId());
    }
}
