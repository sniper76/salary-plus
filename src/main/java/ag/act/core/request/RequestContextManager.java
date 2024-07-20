package ag.act.core.request;

import ag.act.core.filter.appversion.AppVersionChecker;
import ag.act.core.holder.RequestContextHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RequestContextManager {
    private final AppVersionChecker appVersionChecker;

    public void set(HttpServletRequest request, HttpServletResponse response) {
        request.setAttribute("response", response);
        RequestContextHolder.setRequest(request);
        RequestContextHolder.setIsWebAppVersion(appVersionChecker.isWebVersion());
    }

    public void clear() {
        RequestContextHolder.clear();
    }
}
