package ag.act.util;

import ag.act.configuration.security.ActUserProvider;
import ag.act.core.holder.RequestContextHolder;
import ag.act.entity.User;
import ag.act.exception.ActRuntimeException;
import ag.act.exception.ActWarningException;
import ag.act.model.ErrorResponse;
import io.sentry.Sentry;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.event.Level;
import org.springframework.http.HttpStatus;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class LogMessageUtil {

    public static ErrorResponse buildErrorData(final IllegalArgumentException exception, final HttpStatus status) {
        return buildErrorAndResponse(exception, status, Level.WARN);
    }

    public static ErrorResponse buildErrorData(final ActWarningException exception, final HttpStatus status) {
        return buildErrorAndResponse(exception, status, Level.WARN);
    }

    public static ErrorResponse buildErrorData(final Exception exception, final HttpStatus status) {
        return buildErrorAndResponse(exception, status, Level.ERROR);
    }

    public static ErrorResponse buildErrorAndResponse(Exception exception, HttpStatus status, Level level) {

        sendExceptionToSentry(exception);

        try {
            log.makeLoggingEventBuilder(level).log(
                """
                    URI: {} {},
                    UserIP: {},
                    UserID: {},
                    AppVersion: {},
                    UserAgent: {},
                    Query: {},
                    Exception: {} {}
                    """,
                RequestContextHolder.getMethod(),
                RequestContextHolder.getRequestURI(),
                RequestContextHolder.getUserIP(),
                getUserId(),
                RequestContextHolder.getClientAppVersion(),
                RequestContextHolder.getUserAgent(),
                RequestContextHolder.getQueryString(),
                exception.getClass().getSimpleName(),
                exception.getMessage(),
                exception
            );
        } catch (Exception e) {
            log.error("Exception occurred: {}", getExceptionStackTrace(e), e);
            sendExceptionToSentry(e);
        }

        return getErrorResponse(exception, status);
    }

    public static ErrorResponse getErrorResponse(Exception exception, HttpStatus status) {
        return new ErrorResponse()
            .statusCode(status.value())
            .errorCode(getErrorCode(exception).orElse(status.value()))
            .errorData(getErrorData(exception).orElse(null))
            .message(exception.getMessage());
    }

    private static void sendExceptionToSentry(Exception exception) {
        Sentry.configureScope(scope -> {
            scope.setTag("TYPE", "API");

            io.sentry.protocol.User user = new io.sentry.protocol.User();
            user.setId(getUserId());
            user.setIpAddress(RequestContextHolder.getUserIP());
            user.setData(Map.of(
                "AppVersion", String.valueOf(RequestContextHolder.getClientAppVersion()),
                "UserAgent", String.valueOf(RequestContextHolder.getUserAgent())
            ));
            scope.setUser(user);
            scope.setExtra("RequestBody", RequestContextHolder.getRequestBody());
        });
        Sentry.captureException(exception);
    }

    private static Optional<Integer> getErrorCode(Exception exception) {
        if (exception instanceof ActRuntimeException actRuntimeException) {
            return Optional.ofNullable(actRuntimeException.getErrorCode());
        }
        return Optional.empty();
    }

    private static Optional<Map<String, Object>> getErrorData(Exception exception) {
        if (exception instanceof ActRuntimeException actRuntimeException) {
            return Optional.ofNullable(actRuntimeException.getErrorData());
        }
        return Optional.empty();
    }

    private static String getUserId() {
        try {
            return ActUserProvider.get()
                .map(User::getId)
                .map(String::valueOf)
                .orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    public static String getExceptionStackTrace(Throwable ex) {
        final StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));

        return sw.toString();
    }
}
