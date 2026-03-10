package com.example.ticketingproject.common.config;

import com.example.ticketingproject.common.dto.ErrorResponse;
import com.example.ticketingproject.common.enums.ErrorStatus;
import com.example.ticketingproject.common.exception.BaseException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> BaseExceptionHandler(
            BaseException e, HttpServletRequest request
    ) {
        ErrorResponse errorResponse = ErrorResponse.of(
                e.getErrorStatusCode().getHttpStatus(),
                e.getErrorStatusCode().getErrorCode(),
                e.getErrorStatusCode().getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(e.getErrorStatusCode().getHttpStatus()).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class) // DTO @Valid 에러
    public ResponseEntity<ErrorResponse> MethodArgumentNotValidExceptionHandle(
            MethodArgumentNotValidException e, HttpServletRequest request
    ) {
        return getValidationErrorResponse(request);
    }

    // @Validated가 붙은 @RequestParam, @PathVariable 에러
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> ConstraintViolationExceptionHandle(
            ConstraintViolationException e, HttpServletRequest request
    ) {
        return getValidationErrorResponse(request);
    }

    public ResponseEntity<ErrorResponse> getValidationErrorResponse(HttpServletRequest request) {
        ErrorResponse errorResponse = ErrorResponse.of(
                ErrorStatus.VALIDATION_ERROR.getHttpStatus(),
                ErrorStatus.VALIDATION_ERROR.getErrorCode(),
                ErrorStatus.VALIDATION_ERROR.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(ErrorStatus.VALIDATION_ERROR.getHttpStatus()).body(errorResponse);
    }
}
