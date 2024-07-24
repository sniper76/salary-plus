package ag.act.itutil.authentication;

import org.springframework.http.HttpHeaders;

import java.util.Objects;

@SuppressWarnings({"ParameterName", "methodname"})
public class AuthenticationTestUtil {
    public static TestHeader jwt(String jwt) {
        return TestHeader.jwt(jwt);
    }

    public static TestHeader xAppVersion(String xAppVersion) {
        return TestHeader.xAppVersion(xAppVersion);
    }

    public static TestHeader xApiKey(String xApiKey) {
        return TestHeader.xApiKey(xApiKey);
    }

    public static TestHeader userAgent(String userAgent) {
        return TestHeader.userAgent(userAgent);
    }

    public record TestHeader(String headerName, String headerValue) {
        static TestHeader jwt(String jwt) {
            Objects.requireNonNull(jwt, "jwt must not be null");
            return new TestHeader(HttpHeaders.AUTHORIZATION, "Bearer " + jwt);
        }

        static TestHeader xAppVersion(String xAppVersion) {
            Objects.requireNonNull(xAppVersion, "xAppVersion must not be null");
            return new TestHeader("x-app-version", xAppVersion);
        }

        static TestHeader xApiKey(String xApiKey) {
            Objects.requireNonNull(xApiKey, "xApiKey must not be null");
            return new TestHeader("x-api-key", xApiKey);
        }

        static TestHeader userAgent(String userAgent) {
            Objects.requireNonNull(userAgent, "userAgent must not be null");
            return new TestHeader("User-Agent", userAgent);
        }
    }
}
