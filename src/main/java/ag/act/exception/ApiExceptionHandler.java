package ag.act.exception;

import ag.act.enums.SlackChannel;
import ag.act.model.ErrorResponse;
import ag.act.util.LogMessageUtil;
import ag.act.util.SlackMessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class ApiExceptionHandler {

    private final SlackMessageSender slackMessageSender;

    @ExceptionHandler({BadRequestException.class})
    public ResponseEntity<ErrorResponse> handleException(BadRequestException ex) {
        final HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(LogMessageUtil.buildErrorData(ex, httpStatus), httpStatus);
    }

    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        final HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(LogMessageUtil.buildErrorData(ex, httpStatus), httpStatus);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        final ObjectError error = exception.getAllErrors().stream().findFirst().orElse(null);
        final String errorMessage = error == null ? "요청을 처리하던 중 알 수 없는 오류가 발생하였습니다." : error.getDefaultMessage();
        final HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(LogMessageUtil.buildErrorData(new BadRequestException(errorMessage, exception), httpStatus), httpStatus);
    }

    @ExceptionHandler(ActWarningException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(ActWarningException exception) {
        final HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(LogMessageUtil.buildErrorData(exception, httpStatus), httpStatus);
    }

    @ExceptionHandler({DeletedUserException.class})
    public ResponseEntity<ErrorResponse> handleDeletedUserException(DeletedUserException ex) {
        final HttpStatus httpStatus = HttpStatus.GONE;
        return new ResponseEntity<>(LogMessageUtil.buildErrorData(ex, httpStatus), httpStatus);
    }

    @ExceptionHandler({NotHaveStockException.class})
    public ResponseEntity<ErrorResponse> handleNotHaveStockException(NotHaveStockException ex) {
        final HttpStatus httpStatus = HttpStatus.GONE;
        return new ResponseEntity<>(LogMessageUtil.buildErrorData(ex, httpStatus), httpStatus);
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<ErrorResponse> handleRuntimeException(Exception ex) {
        final HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        return new ResponseEntity<>(LogMessageUtil.buildErrorData(
            new InternalServerException("알 수 없는 오류가 발생하였습니다. 잠시 후에 다시 이용해 주세요.", ex),
            httpStatus
        ), httpStatus);
    }

    @ExceptionHandler(InternalServerException.class)
    public ResponseEntity<ErrorResponse> handleInternalServerException(InternalServerException ex) {
        final HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        return new ResponseEntity<>(LogMessageUtil.buildErrorData(ex, httpStatus), httpStatus);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        final HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        return new ResponseEntity<>(LogMessageUtil.buildErrorData(
            new InternalServerException("알 수 없는 오류가 발생하였습니다. 잠시 후에 다시 이용해 주세요.", ex),
            httpStatus
        ), httpStatus);
    }

    @ExceptionHandler(BatchExecutionException.class)
    public ResponseEntity<ErrorResponse> handleBatchExecutionException(BatchExecutionException ex) {
        final HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        slackMessageSender.sendSlackMessage(ex.getMessage(), SlackChannel.ACT_BATCH_ALERT);
        return new ResponseEntity<>(LogMessageUtil.buildErrorData(ex, httpStatus), httpStatus);
    }

    @ExceptionHandler(KcbOkCertException.class)
    public ResponseEntity<ErrorResponse> handleKcbOkCertException(KcbOkCertException ex) {
        final HttpStatus httpStatus = HttpStatus.UNAUTHORIZED;
        return new ResponseEntity<>(LogMessageUtil.buildErrorData(ex, httpStatus), httpStatus);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(UnauthorizedException ex) {
        final HttpStatus httpStatus = HttpStatus.UNAUTHORIZED;
        return new ResponseEntity<>(LogMessageUtil.buildErrorData(ex, httpStatus), httpStatus);
    }

    @ExceptionHandler({ForbiddenException.class, HttpClientErrorException.Forbidden.class})
    public ResponseEntity<ErrorResponse> handleForbiddenException(ForbiddenException ex) {
        final HttpStatus httpStatus = HttpStatus.FORBIDDEN;
        return new ResponseEntity<>(LogMessageUtil.buildErrorData(ex, httpStatus), httpStatus);
    }

    @ExceptionHandler({UserNotHaveStockException.class})
    public ResponseEntity<ErrorResponse> handleUserNotHaveStockException(UserNotHaveStockException ex) {
        final HttpStatus httpStatus = HttpStatus.FORBIDDEN;
        return new ResponseEntity<>(LogMessageUtil.buildErrorData(ex, httpStatus), httpStatus);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException ex) {
        final HttpStatus httpStatus = HttpStatus.NOT_FOUND;
        return new ResponseEntity<>(LogMessageUtil.buildErrorData(ex, httpStatus), httpStatus);
    }

    @ExceptionHandler(TooManyRequestsException.class)
    public ResponseEntity<ErrorResponse> handleTooManyRequestsException(TooManyRequestsException ex) {
        final HttpStatus httpStatus = HttpStatus.TOO_MANY_REQUESTS;
        return new ResponseEntity<>(LogMessageUtil.buildErrorData(ex, httpStatus), httpStatus);
    }

    //=========================== getErrorResponse without Sentry ============================

    @ExceptionHandler(VerificationPinNumberException.class)
    public ResponseEntity<ErrorResponse> handleVerificationPinNumberException(VerificationPinNumberException ex) {
        final HttpStatus httpStatus = HttpStatus.UNAUTHORIZED;
        return new ResponseEntity<>(LogMessageUtil.getErrorResponse(ex, httpStatus), httpStatus);
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(Exception ex) {
        final HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(LogMessageUtil.getErrorResponse(new BadRequestException("요청 데이터를 확인해주세요.", ex), httpStatus), httpStatus);
    }

    @ExceptionHandler({MissingRequestHeaderException.class})
    public ResponseEntity<ErrorResponse> handleMissingRequestHeaderException(Exception ex) {
        final HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(LogMessageUtil.getErrorResponse(new BadRequestException("요청 헤더를 확인해주세요.", ex), httpStatus), httpStatus);
    }

    @ExceptionHandler({HttpMediaTypeNotAcceptableException.class})
    public ResponseEntity<ErrorResponse> handleHttpMediaTypeNotAcceptableException(HttpMediaTypeNotAcceptableException ex) {
        final HttpStatus httpStatus = HttpStatus.UNSUPPORTED_MEDIA_TYPE;
        return new ResponseEntity<>(LogMessageUtil.getErrorResponse(ex, httpStatus), httpStatus);
    }

    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        final HttpStatus httpStatus = HttpStatus.METHOD_NOT_ALLOWED;
        return new ResponseEntity<>(LogMessageUtil.getErrorResponse(ex, httpStatus), httpStatus);
    }

    @ExceptionHandler({UpgradeRequiredException.class})
    public ResponseEntity<ErrorResponse> handleAppVersionVerificationException(UpgradeRequiredException ex) {
        final HttpStatus httpStatus = HttpStatus.UPGRADE_REQUIRED;
        return new ResponseEntity<>(LogMessageUtil.getErrorResponse(ex, httpStatus), httpStatus);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandlerFound(NoHandlerFoundException ex) {
        // spring:
        //   web:
        //     resources:
        //       add-mappings: false
        //   mvc:
        //     throw-exception-if-no-handler-found: true
        final HttpStatus httpStatus = HttpStatus.NOT_FOUND;
        return new ResponseEntity<>(LogMessageUtil.getErrorResponse(ex, httpStatus), httpStatus);
    }
}