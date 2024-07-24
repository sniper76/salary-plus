package ag.act.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.stream.Stream;

@Configuration
public class WebRequestMatcherConfig {

    @Bean
    public AntPathRequestMatcher[] permitAllPathRequestMatchers() {
        return Stream.concat(
                Stream.of(permitNoAppVersionCheckPathRequestMatchers()),
                Stream.of(permitAppVersionCheckPathRequestMatchers())
            )
            .toArray(AntPathRequestMatcher[]::new);
    }

    @Bean
    public AntPathRequestMatcher[] permitNoAppVersionCheckPathRequestMatchers() {
        return new AntPathRequestMatcher[] {
            new AntPathRequestMatcher("/"),
            new AntPathRequestMatcher("/api/health"),
            new AntPathRequestMatcher("/api/batch/**"),
            new AntPathRequestMatcher("/public-api/**"),
            new AntPathRequestMatcher("/api/callback/**"),
            new AntPathRequestMatcher("/api/public/**"),
            new AntPathRequestMatcher("/api/admin/auth/**"),
            new AntPathRequestMatcher("/api/admin/data-matrices/**"),
            new AntPathRequestMatcher("/swagger-ui"),
            new AntPathRequestMatcher("/swagger-ui/**"),
            new AntPathRequestMatcher("/swagger-resources/**"),
            new AntPathRequestMatcher("/v3/api-docs"),
            new AntPathRequestMatcher("/v3/api-docs/**"),
            new AntPathRequestMatcher("/openapi.yaml"),
            new AntPathRequestMatcher("/api/auth/web/**")
        };
    }

    @Bean
    public AntPathRequestMatcher[] permitAppVersionCheckPathRequestMatchers() {
        return new AntPathRequestMatcher[] {
            new AntPathRequestMatcher("/api/sms/**"),
            new AntPathRequestMatcher("/api/contact-us"),
        };
    }
}
