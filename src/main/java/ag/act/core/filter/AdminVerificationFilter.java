package ag.act.core.filter;

import ag.act.core.filter.appversion.AppVersionChecker;
import ag.act.core.filter.response.ErrorResponseResolver;
import ag.act.core.holder.RequestContextHolder;
import ag.act.entity.User;
import ag.act.enums.RoleType;
import ag.act.exception.ForbiddenException;
import ag.act.service.user.UserRoleService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@Order(20)
public class AdminVerificationFilter extends AbstractUserRoleVerificationFilter {

    private final UserRoleService userRoleService;
    private final AppVersionChecker appVersionChecker;

    @Autowired
    public AdminVerificationFilter(
        ErrorResponseResolver errorResponseResolver,
        UserRoleService userRoleService,
        AppVersionChecker appVersionChecker
    ) {
        super(
            errorResponseResolver,
            List.of(new AntPathRequestMatcher("/api/admin/**")),
            List.of(new AntPathRequestMatcher("/api/admin/auth/**"))
        );
        this.userRoleService = userRoleService;
        this.appVersionChecker = appVersionChecker;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

        setIsCmsApi(request);

        super.doFilterInternal(request, response, filterChain);
    }

    private void setIsCmsApi(HttpServletRequest request) {
        try {
            RequestContextHolder.setIsCmsApi(appVersionChecker.isCmsVersion(), isAdminApiPath(request));
        } catch (Exception e) {
            log.warn("Failed to check if the request is from CMS", e);
        }
    }

    private boolean isAdminApiPath(HttpServletRequest request) {
        return permitPathRequestMatchers.stream()
            .anyMatch(antPathRequestMatcher -> antPathRequestMatcher.matches(request));
    }

    @Override
    protected void validateUserRole(User user) {
        if (!userRoleService.hasAnyRole(user.getId(), RoleType.ADMIN, RoleType.SUPER_ADMIN, RoleType.ACCEPTOR_USER)) {
            throw new ForbiddenException(FORBIDDEN_ERROR_MESSAGE);
        }
    }
}
