package rmsbackend.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import rmsbackend.common.generic.StatusCode;
import rmsbackend.common.util.JSONRespond;
import rmsbackend.dto.RespondDTO;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public RespondDTO handleResourceNotFoundException(ResourceNotFoundException exception) {
        log.warn(exception.getMessage());

        return JSONRespond.error(StatusCode.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public RespondDTO handleDuplicateKeyException(DuplicateResourceException exception) {
        log.warn(exception.getMessage());

        return JSONRespond.error(StatusCode.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(BusinessException.class)
    public RespondDTO handleBusinessException(BusinessException exception) {
        log.warn(exception.getMessage());

        return JSONRespond.error(StatusCode.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public RespondDTO handleIllegalArgumentException(IllegalArgumentException exception) {
        log.warn(exception.getMessage());

        return JSONRespond.error(StatusCode.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public RespondDTO handleValidationException(MethodArgumentNotValidException exception) {
        String errorMessage = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .orElse("Validation Failed");

        return JSONRespond.error(StatusCode.BAD_REQUEST, errorMessage);
    }

    // Let Spring Security's accessDeniedHandler answer with 403 instead of the catch-all 500 below.
    @ExceptionHandler(AccessDeniedException.class)
    public void handleAccessDenied(AccessDeniedException exception) {
        throw exception;
    }

    @ExceptionHandler(Exception.class)
    public RespondDTO handleException(Exception exception) {
        log.error("Unexpected error", exception);

        return JSONRespond.error(StatusCode.INTERNAL_ERROR, exception.getMessage());
    }

}
