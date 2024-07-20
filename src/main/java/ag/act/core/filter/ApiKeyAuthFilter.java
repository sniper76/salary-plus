package ag.act.core.filter;

interface ApiKeyAuthFilter {
    String NA_CREDENTIALS = "N/A";
    String DEFAULT_X_API_HEADER_KEY = "x-api-key";
    String ERROR_MESSAGE = "인가되지 않은 접근입니다.";
}
