package com.scriptopia.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.scriptopia.demo.dto.exception.ErrorResponse;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();

        ErrorCode errorCode = ErrorCode.E_400; // 기본값

        if (fieldError != null && "email".equals(fieldError.getField())) {
            if (Objects.equals(fieldError.getCode(), "NotBlank")) {
                errorCode = ErrorCode.REQ_400_MISSING_EMAIL;
            } else if (Objects.equals(fieldError.getCode(), "Email")) {
                errorCode = ErrorCode.REQ_400_INVALID_EMAIL_FORMAT;
            }
        }


        return ResponseEntity
                .status(errorCode.getStatus())
                .body(new ErrorResponse(errorCode));
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(final CustomException e) {
        ErrorCode errorCode = e.getErrorCode();

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(new ErrorResponse(errorCode));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(ErrorCode.E_500));
    }
}
