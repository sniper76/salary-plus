package ag.act.core.filter;

import ag.act.configuration.security.ActUserProvider;
import ag.act.core.filter.response.ErrorResponseResolver;
import ag.act.entity.User;
import ag.act.enums.ActErrorCode;
import ag.act.exception.ForbiddenException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@SuppressWarnings("NullableProblems")
@RequiredArgsConstructor
public abstract class AbstractUserRoleVerificationFilter extends OncePerRequestFilter {

    protected static final String FORBIDDEN_ERROR_MESSAGE = "인가되지 않은 접근입니다.";
    protected static final String PASSWORD_CHANGE_REQUIRED_MESSAGE = "비밀번호 변경이 필요합니다.";
    protected final ErrorResponseResolver errorResponseResolver;
    protected final List<AntPathRequestMatcher> permitPathRequestMatchers;
    protected final List<AntPathRequestMatcher> whiteListPathRequestMatchers;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

        final boolean isInWhiteList = whiteListPathRequestMatchers.stream()
            .anyMatch(antPathRequestMatcher -> antPathRequestMatcher.matches(request));

        if (isInWhiteList) {
            filterChain.doFilter(request, response);
            return;
        }

        final boolean validateRequired = permitPathRequestMatchers.stream()
            .anyMatch(antPathRequestMatcher -> antPathRequestMatcher.matches(request));

        if (validateRequired) {
            try {
                validate();
            } catch (ForbiddenException ex) {
                errorResponseResolver.setErrorResponse(response, ex);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private void validate() {

        if (ActUserProvider.get().isEmpty()) {
            throw new ForbiddenException(FORBIDDEN_ERROR_MESSAGE);
        }

        final User user = ActUserProvider.getNoneNull();
        validateUserRole(user);

        if (Boolean.TRUE == user.getIsChangePasswordRequired()) {
            throw new ForbiddenException(ActErrorCode.IS_CHANGE_PASSWORD_REQUIRED.getCode(), PASSWORD_CHANGE_REQUIRED_MESSAGE);
        }
    }

    protected abstract void validateUserRole(User user);
}
