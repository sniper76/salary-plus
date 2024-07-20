package ag.act.core.filter;

import ag.act.core.request.RequestContextManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@SuppressWarnings("NullableProblems")
@Slf4j
@Component
@RequiredArgsConstructor
@Order(1)
public class RequestContextFilter extends OncePerRequestFilter {

    private final RequestContextManager requestContextManager;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        requestContextManager.set(request, response);
        filterChain.doFilter(request, response);
    }
}
