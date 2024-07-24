package ag.act.core.holder.web;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class WebBrowserDetector {

    private final HttpServletRequest request;

    public boolean isRequestFromWebBrowser() {
        try {
            return getUserAgent()
                .map(this::isWebRequest)
                .orElse(false);
        } catch (Exception e) {
            // ignore
        }
        return false;
    }

    private boolean isWebRequest(String userAgent) {
        return isKnownBrowser(userAgent)
            || isPostmanOnLocalOrDevServer(userAgent);
    }

    private boolean isPostmanOnLocalOrDevServer(String userAgent) {
        return (isLocalhost() || isDevServer())
            && isPostmanRuntimeUserAgent(userAgent);
    }

    private boolean isPostmanRuntimeUserAgent(String userAgent) {
        return userAgent.contains("PostmanRuntime");
    }

    private boolean isDevServer() {
        return isServerOf("devapi.act.ag");
    }

    private boolean isLocalhost() {
        return isServerOf("localhost");
    }

    private Boolean isServerOf(String hostName) {
        return getHost()
            .map(host -> host.contains(hostName))
            .orElse(false);
    }

    private boolean isKnownBrowser(String userAgent) {
        return userAgent.contains("Mozilla/");
    }

    private Optional<String> getHost() {
        return Optional.ofNullable(request.getHeader("Host"));
    }

    private Optional<String> getUserAgent() {
        return Optional.ofNullable(request.getHeader("User-Agent"));
    }
}
