package ag.act;

import com.google.firebase.messaging.FirebaseMessaging;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequestWrapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static org.mockito.Mockito.mock;

@Profile({"test-persistence", "test"})
public class TestConfiguration {

    @Bean("firebaseMessaging")
    public FirebaseMessaging firebaseMessaging() {
        return mock(FirebaseMessaging.class);
    }

    @Bean
    public Filter mockHttpServletRequestFilter() {
        return new MockHttpServletRequestFilter();
    }

    @SuppressWarnings("NullableProblems")
    @Order(0)
    static class MockHttpServletRequestFilter extends OncePerRequestFilter {

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

            Object req = request;
            while (req instanceof ServletRequestWrapper) {
                req = ((ServletRequestWrapper) req).getRequest();
            }

            if (req instanceof MockHttpServletRequest mockHttpServletRequest) {
                setIfNull(mockHttpServletRequest, "User-Agent", "Mozilla/5.0");
                setIfNull(mockHttpServletRequest, "Host", "localhost");
            }

            filterChain.doFilter(request, response);
        }

        private void setIfNull(
            final MockHttpServletRequest mockHttpServletRequest,
            final String headerName,
            final String headerValue
        ) {
            if (mockHttpServletRequest.getHeader(headerName) == null) {
                mockHttpServletRequest.addHeader(headerName, headerValue);
            }
        }

    }
}
