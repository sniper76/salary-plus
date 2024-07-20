package ag.act.core.filter.appversion;

import ag.act.core.holder.RequestContextHolder;
import ag.act.core.holder.web.WebBrowserDetectorFactory;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AppVersionChecker {

    private static final String CMS_VERSION = "CMS";
    private static final String WEB_VERSION = "WEB";
    private final WebBrowserDetectorFactory webBrowserDetectorFactory;

    public boolean isCmsVersion() {
        return isRequestFromWebBrowser()
            && CMS_VERSION.equals(getClientAppVersion());
    }

    public boolean isWebVersion() {
        return isRequestFromWebBrowser()
            && WEB_VERSION.equals(getClientAppVersion());
    }

    private boolean isRequestFromWebBrowser() {
        return webBrowserDetectorFactory
            .createWebBrowserDetector(getRequest())
            .isRequestFromWebBrowser();
    }

    private String getClientAppVersion() {
        return RequestContextHolder.getClientAppVersion();
    }

    private HttpServletRequest getRequest() {
        return RequestContextHolder.getRequest();
    }
}
