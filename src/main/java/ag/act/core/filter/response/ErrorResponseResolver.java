package ag.act.core.filter.response;

import ag.act.exception.ForbiddenException;
import ag.act.exception.UnauthorizedException;
import ag.act.exception.UpgradeRequiredException;
import ag.act.util.LogMessageUtil;
import ag.act.util.ObjectMapperUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.io.IOException;

@Component
public class ErrorResponseResolver {
    private final ObjectMapperUtil objectMapperUtil;

    public ErrorResponseResolver(ObjectMapperUtil objectMapperUtil) {
        this.objectMapperUtil = objectMapperUtil;
    }

    public void setErrorResponse(HttpServletResponse response, ForbiddenException ex) throws IOException {
        final HttpStatus httpStatus = HttpStatus.FORBIDDEN;
        final ag.act.model.ErrorResponse errorResponse = LogMessageUtil.buildErrorData(ex, httpStatus);
        writeErrorToResponse(response, httpStatus, errorResponse);
    }

    public void setErrorResponse(HttpServletResponse response, UnauthorizedException ex) throws IOException {
        final HttpStatus httpStatus = HttpStatus.UNAUTHORIZED;
        final ag.act.model.ErrorResponse errorResponse = LogMessageUtil.buildErrorData(ex, httpStatus);
        writeErrorToResponse(response, httpStatus, errorResponse);
    }

    public void setErrorResponse(HttpServletResponse response, UpgradeRequiredException ex) throws IOException {
        final HttpStatus httpStatus = HttpStatus.UPGRADE_REQUIRED;
        final ag.act.model.ErrorResponse errorResponse = LogMessageUtil.getErrorResponse(ex, httpStatus);
        writeErrorToResponse(response, httpStatus, errorResponse);
    }

    public void setErrorResponse(HttpServletResponse response, NoHandlerFoundException ex) throws IOException {
        final HttpStatus httpStatus = HttpStatus.NOT_FOUND;
        final ag.act.model.ErrorResponse errorResponse = LogMessageUtil.getErrorResponse(ex, httpStatus);
        writeErrorToResponse(response, httpStatus, errorResponse);
    }

    private void writeErrorToResponse(
        HttpServletResponse response,
        HttpStatus httpStatus,
        ag.act.model.ErrorResponse errorResponse
    ) throws IOException {
        final String errorJson = objectMapperUtil.toJson(errorResponse);

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setStatus(httpStatus.value());
        response.getWriter().write(errorJson);
        response.getWriter().flush();
    }
}
