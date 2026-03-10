package com.example.ticketingproject.common.config;

import com.example.ticketingproject.common.dto.ErrorResponse;
import com.example.ticketingproject.common.dto.ValidationError;
import com.example.ticketingproject.common.dto.ValidationErrorResponse;
import com.example.ticketingproject.common.enums.ErrorStatus;
import com.example.ticketingproject.common.exception.BaseException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

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
    public ResponseEntity<ValidationErrorResponse> MethodArgumentNotValidExceptionHandle(
            MethodArgumentNotValidException e, HttpServletRequest request
    ) {
        List<ValidationError> messages = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new ValidationError(error.getField(), error.getDefaultMessage()))
                .toList();

        ValidationErrorResponse validationErrorResponse = ValidationErrorResponse.of(
                ErrorStatus.VALIDATION_ERROR.getHttpStatus(),
                ErrorStatus.VALIDATION_ERROR.getErrorCode(),
                messages,
                request.getRequestURI()
        );

        return ResponseEntity.status(ErrorStatus.VALIDATION_ERROR.getHttpStatus()).body(validationErrorResponse);
    }

    // @Validated가 붙은 @RequestParam, @PathVariable 에러
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ValidationErrorResponse> ConstraintViolationExceptionHandle(
            ConstraintViolationException e, HttpServletRequest request
    ) {
        List<ValidationError> messages = e.getConstraintViolations()
                .stream()
                .map(violation -> new ValidationError(
                        violation.getPropertyPath().toString(),
                        violation.getMessage()
                ))
                .toList();

        ValidationErrorResponse validationErrorResponse = ValidationErrorResponse.of(
                ErrorStatus.VALIDATION_ERROR.getHttpStatus(),
                ErrorStatus.VALIDATION_ERROR.getErrorCode(),
                messages,
                request.getRequestURI()
        );

        return ResponseEntity.status(ErrorStatus.VALIDATION_ERROR.getHttpStatus()).body(validationErrorResponse);
    }
}
