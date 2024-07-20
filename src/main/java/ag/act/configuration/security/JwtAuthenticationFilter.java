package ag.act.configuration.security;

import ag.act.core.filter.response.ErrorResponseResolver;
import ag.act.entity.User;
import ag.act.exception.NotFoundException;
import ag.act.exception.UnauthorizedException;
import ag.act.service.user.UserRoleService;
import ag.act.service.user.UserService;
import ag.act.util.StatusUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@AllArgsConstructor
@Component
@Order(10)
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;
    private final UserService userService;
    private final ErrorResponseResolver errorResponseResolver;
    private final UserRoleService userRoleService;
    private final AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource = new WebAuthenticationDetailsSource();

    @SuppressWarnings("NullableProblems")
    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            final String token = tokenProvider.parseBearerToken(request);

            if (token != null && !token.equalsIgnoreCase("null")) {

                final User user = getValidUser(token);

                final AbstractAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    user,
                    null,
                    AuthorityUtils.NO_AUTHORITIES
                );
                authentication.setDetails(authenticationDetailsSource.buildDetails(request));
                SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
                securityContext.setAuthentication(authentication);
                SecurityContextHolder.setContext(securityContext);
            }
        } catch (UnauthorizedException ex) {
            log.warn("JwtAuthenticationFilter request: {} : {}", request.getRequestURI(), ex.getMessage());
            errorResponseResolver.setErrorResponse(response, ex);
            return;
        } catch (ExpiredJwtException expired) {
            log.warn("JwtAuthenticationFilter request: {} : {}", request.getRequestURI(), expired.getMessage());
            errorResponseResolver.setErrorResponse(response, new UnauthorizedException("토큰이 만료되었습니다. 다시 로그인 후에 이용해주세요.", expired));
            return;
        } catch (Exception ex) {
            log.error("JwtAuthenticationFilter request: {} : {}", request.getRequestURI(), ex.getMessage());
            errorResponseResolver.setErrorResponse(response, new UnauthorizedException("로그인 후에 이용해주세요.", ex));
            return;
        }

        filterChain.doFilter(request, response);
    }

    private User getValidUser(String token) {
        final Long userId = tokenProvider.validateAndGetUserId(token);
        if (userId == null) {
            throw new UnauthorizedException("로그인 후에 이용해주세요.");
        }

        final User user = userService.findUser(userId).orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다."));

        if (StatusUtil.isDeletedOrWithdrawal(user.getStatus())) {
            throw new UnauthorizedException("탈퇴한 회원입니다.");
        }

        user.setAdmin(userRoleService.isAdmin(user.getId()));
        user.setAcceptor(userRoleService.isAcceptorUser(user.getId()));
        
        return user;
    }
}