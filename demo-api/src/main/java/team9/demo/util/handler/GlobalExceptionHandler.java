package team9.demo.util.handler;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.NoHandlerFoundException;
import team9.demo.error.*;
import team9.demo.response.ErrorResponse;
import team9.demo.util.helper.ResponseHelper;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<?> handleException(Exception e, ErrorCode errorCode, HttpStatus status) {
        log.info("ErrorCode: {}, Message: {}, Class: {}", errorCode.getCode(), errorCode.getMessage(), e.getStackTrace()[0].getClassName());
        return ResponseHelper.error(status, ErrorResponse.from(errorCode));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    protected ResponseEntity<?> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        return handleException(e, ErrorCode.VARIABLE_WRONG, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<?> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.warn("HTTP Method Not Supported: {} is not supported. Supported: {}", e.getMethod(), e.getSupportedHttpMethods());
        return handleException(e, ErrorCode.PATH_WRONG, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    protected ResponseEntity<?> handleNoHandlerFoundException(NoHandlerFoundException e) {
        log.warn("HTTP URL Not Supported: {}", e.getRequestURL());
        return handleException(e, ErrorCode.PATH_WRONG, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public void handleAccessDeniedException(AccessDeniedException ex) {
        log.error("Access Denied: {}", ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException e) {
        return handleException(e, ErrorCode.VARIABLE_WRONG, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthorizationException.class)
    protected ResponseEntity<?> handleAuthorizationException(AuthorizationException e) {
        return handleException(e, e.getErrorCode(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException e, HttpServletRequest request) {
        log.warn("HttpMessageNotReadableException: method={}, url={}, remoteAddr={}, message={}",
                request.getMethod(), request.getRequestURI(), request.getRemoteAddr(), e.getMessage());
        return handleException(e, ErrorCode.VARIABLE_WRONG, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    protected ResponseEntity<?> handleNotFoundException(NotFoundException e) {
        return handleException(e, e.getErrorCode(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ConflictException.class)
    protected ResponseEntity<?> handleConflictException(ConflictException e) {
        return handleException(e, e.getErrorCode(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InsufficientAuthenticationException.class)
    protected ResponseEntity<?> handleInsufficientAuthenticationException() {
        return handleException(new InsufficientAuthenticationException("Unauthorized"), ErrorCode.NOT_AUTHORIZED, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<?> handleGenericException(Exception e) {
        log.error("예기치 않은 오류 발생: {}", e.getMessage(), e);
        return ResponseHelper.error(HttpStatus.INTERNAL_SERVER_ERROR, ErrorResponse.from(ErrorCode.INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler(AiException.class)
    protected ResponseEntity<?> handleAiException(AiException e) {
        return handleException(e, e.getErrorCode(), HttpStatus.BAD_REQUEST);
    }
}