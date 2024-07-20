package ag.act.core.filter;

import ag.act.configuration.security.ActUserProvider;
import ag.act.core.filter.response.ErrorResponseResolver;
import ag.act.entity.User;
import ag.act.exception.ForbiddenException;
import ag.act.util.KoreanDateTimeUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@Order(30)
public class PinNumberVerificationFilter extends OncePerRequestFilter {

    private final ErrorResponseResolver errorResponseResolver;
    private final List<AntPathRequestMatcher> whiteListPathRequestMatchers;

    @Autowired
    public PinNumberVerificationFilter(ErrorResponseResolver errorResponseResolver, AntPathRequestMatcher[] permitAllPathRequestMatchers) {
        this.errorResponseResolver = errorResponseResolver;

        final List<AntPathRequestMatcher> antPathRequestMatchers = List.of(
            new AntPathRequestMatcher("/api/users/me"),
            new AntPathRequestMatcher("/api/auth/logout"),
            new AntPathRequestMatcher("/api/auth/reset-pin-number"),
            new AntPathRequestMatcher("/api/auth/verify-pin-number"),
            new AntPathRequestMatcher("/api/auth/register-pin-number"),
            new AntPathRequestMatcher("/api/auth/register-user-info"),
            new AntPathRequestMatcher("/api/admin/**")
        );

        this.whiteListPathRequestMatchers = new ArrayList<>();
        this.whiteListPathRequestMatchers.addAll(antPathRequestMatchers);
        this.whiteListPathRequestMatchers.addAll(Arrays.asList(permitAllPathRequestMatchers));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

        if (!isInWhiteList(request)) {
            try {
                validateUserPinNumberVerificationExpiration();
            } catch (ForbiddenException ex) {
                errorResponseResolver.setErrorResponse(response, ex);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isInWhiteList(HttpServletRequest request) {
        return whiteListPathRequestMatchers
            .stream()
            .anyMatch(antPathRequestMatcher -> antPathRequestMatcher.matches(request));
    }

    private void validateUserPinNumberVerificationExpiration() {

        final Optional<User> user = ActUserProvider.get();

        if (user.isEmpty() || user.get().getLastPinNumberVerifiedAt() == null) {
            return;
        }

        if (KoreanDateTimeUtil.isBeforeTodayKoreanTime(user.get().getLastPinNumberVerifiedAt())) {
            throw new ForbiddenException("핀번호 검증토큰이 만료되었습니다.");
        }
    }
}
