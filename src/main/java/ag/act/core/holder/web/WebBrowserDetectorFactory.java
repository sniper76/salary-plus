package ag.act.core.holder.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class WebBrowserDetectorFactory {

    public WebBrowserDetector createWebBrowserDetector(HttpServletRequest request) {
        return new WebBrowserDetector(request);
    }
}
