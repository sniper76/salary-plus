package ag.act.core.filter;

import ag.act.core.filter.appversion.AppVersionChecker;
import ag.act.core.filter.response.ErrorResponseResolver;
import ag.act.core.holder.RequestContextHolder;
import ag.act.dto.AppVersion;
import ag.act.enums.AppPreferenceType;
import ag.act.exception.AppVersionVerificationException;
import ag.act.exception.UpgradeRequiredException;
import ag.act.module.cache.AppPreferenceCache;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Component
@Order(2)
public class AppVersionVerificationFilter extends OncePerRequestFilter {
    private final ErrorResponseResolver errorResponseResolver;
    private final AppVersionChecker appVersionChecker;
    private final AppPreferenceCache appPreferenceCache;
    private final List<AntPathRequestMatcher> whiteListPathRequestMatchers;

    public AppVersionVerificationFilter(
        ErrorResponseResolver errorResponseResolver,
        AppVersionChecker appVersionChecker,
        AppPreferenceCache appPreferenceCache,
        AntPathRequestMatcher[] permitNoAppVersionCheckPathRequestMatchers
    ) {
        this.errorResponseResolver = errorResponseResolver;
        this.appVersionChecker = appVersionChecker;
        this.appPreferenceCache = appPreferenceCache;
        this.whiteListPathRequestMatchers = combineWhiteList(permitNoAppVersionCheckPathRequestMatchers);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

        final boolean isInWhiteList = whiteListPathRequestMatchers.stream()
            .anyMatch(antPathRequestMatcher -> antPathRequestMatcher.matches(request));

        if (isInWhiteList) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            validateClientAppVersion();
        } catch (UpgradeRequiredException ex) {
            errorResponseResolver.setErrorResponse(response, ex);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private List<AntPathRequestMatcher> combineWhiteList(AntPathRequestMatcher[] permitNoAppVersionCheckPathRequestMatchers) {
        return Stream
            .concat(
                Stream.of(permitNoAppVersionCheckPathRequestMatchers),
                Stream.of(new AntPathRequestMatcher("/actuator/**"))
            )
            .toList();
    }

    private void validateClientAppVersion() {
        if (isByPassAppVersion()) {
            return;
        }

        if (!isSupportClientAppVersion()) {
            throw new AppVersionVerificationException();
        }
    }

    private boolean isByPassAppVersion() {
        return isPublicApi()
            || isCmsVersion()
            || isWebVersion();
    }

    private boolean isPublicApi() {
        return RequestContextHolder.isPublicApi();
    }

    private boolean isWebVersion() {
        return RequestContextHolder.isWebAppVersion() || appVersionChecker.isWebVersion();
    }

    private boolean isCmsVersion() {
        return RequestContextHolder.isCmsApi() || appVersionChecker.isCmsVersion();
    }

    private boolean isSupportClientAppVersion() {
        final AppVersion minAppVersion = appPreferenceCache.getValue(AppPreferenceType.MIN_APP_VERSION);
        final AppVersion clientAppVersion = AppVersion.of(RequestContextHolder.getClientAppVersion());

        return clientAppVersion.compareTo(minAppVersion) >= 0;
    }
}
