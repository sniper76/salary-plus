package ag.act.core.holder;

import ag.act.enums.ClientType;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;


@SuppressWarnings({"AbbreviationAsWordInName"})
public class RequestContextHolder {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final ThreadLocal<HttpServletRequest> requestHolder = new ThreadLocal<>();
    private static final String PARAMETER_MAP = "parameterMap";
    private static final String IS_CMS_API = "isCmsApi";
    private static final String IS_WEB_APP_VERSION = "isWebAppVersion";
    private static final String IS_PUBLIC_API = "isPublicApi";
    private static final String RESPONSE = "response";
    private static final String X_APP_VERSION = "x-app-version";
    private static final String USER_AGENT = "User-Agent";
    private static final String X_FORWARDED_FOR = "X-Forwarded-For";

    public static void setRequest(HttpServletRequest request) {
        requestHolder.set(request);
    }

    public static HttpServletRequest getRequest() {
        return requestHolder.get();
    }

    public static String getUserIP() {
        try {
            final String ipAddress = getUserIpFromHeader();
            if (ipAddress != null) {
                return ipAddress;
            }
            return getRequest().getRemoteAddr();
        } catch (Exception e) {
            return null;
        }
    }

    private static String getUserIpFromHeader() {
        final HttpServletRequest request = getRequest();
        final String ipAddress = request.getHeader(X_FORWARDED_FOR);

        if (StringUtils.isBlank(ipAddress)) {
            return request.getRemoteAddr();
        }

        return ipAddress.split(",")[0].trim();
    }

    public static String getRequestURI() {
        try {
            return getRequest().getRequestURI();
        } catch (Exception e) {
            return null;
        }
    }

    public static String getMethod() {
        try {
            return getRequest().getMethod();
        } catch (Exception e) {
            return null;
        }
    }

    public static String getUserAgent() {
        try {
            return getRequest().getHeader(USER_AGENT);
        } catch (Exception e) {
            return null;
        }
    }

    public static String getQueryString() {
        try {
            return getRequest().getQueryString();
        } catch (Exception e) {
            return null;
        }
    }

    public static String getClientAppVersion() {
        try {
            return getRequest().getHeader(X_APP_VERSION);
        } catch (Exception e) {
            return null;
        }
    }

    public static ClientType getClientType() {
        if (isCmsApi()) {
            return ClientType.CMS;
        } else if (isWebAppVersion()) {
            return ClientType.WEB;
        } else {
            return ClientType.APP;
        }
    }

    public static String getRequestBody() {
        try {
            return objectMapper.writeValueAsString(getRequest().getAttribute(PARAMETER_MAP));
        } catch (Exception e) {
            return "UnsupportedOperationException for reading request body.";
        }
    }

    public static void setRequestParameterMap(Map<String, Object> parameterMap) {
        getRequest().setAttribute(PARAMETER_MAP, parameterMap);
    }

    public static boolean isCmsApi() {
        final Object isCmsApi = getRequest().getAttribute(IS_CMS_API);
        return isCmsApi != null && (boolean) isCmsApi;
    }

    public static void setIsCmsApi(boolean isCmsApi, final boolean adminApiPath) {
        getRequest().setAttribute(IS_CMS_API, isCmsApi && adminApiPath);
    }

    public static boolean isWebAppVersion() {
        final Object isWebAppVersion = getRequest().getAttribute(IS_WEB_APP_VERSION);
        return isWebAppVersion != null && (boolean) isWebAppVersion;
    }

    public static void setIsWebAppVersion(boolean isWebAppVersion) {
        getRequest().setAttribute(IS_WEB_APP_VERSION, isWebAppVersion);
    }

    public static boolean isPublicApi() {
        final Object isPublicApi = getRequest().getAttribute(IS_PUBLIC_API);
        return isPublicApi != null && (boolean) isPublicApi;
    }

    public static void setIsPublicApi(final boolean isPublicApi) {
        getRequest().setAttribute(IS_PUBLIC_API, isPublicApi);
    }


    public static HttpServletResponse getResponse() {
        try {
            return (HttpServletResponse) getRequest().getAttribute(RESPONSE);
        } catch (Exception e) {
            return null;
        }
    }

    public static void clear() {
        requestHolder.remove();
    }
}
