package com.scriptopia.demo.exception;

import io.jsonwebtoken.ExpiredJwtException;
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

        ErrorCode errorCode = ErrorCode.E_400;
        if (fieldError == null || fieldError.getDefaultMessage() == null) {
             // 기본값
        }
        else {
            if ("email".equals(fieldError.getField())){
                if (Objects.equals(fieldError.getCode(), "NotBlank")) {
                    errorCode = ErrorCode.E_400_MISSING_EMAIL;
                } else if (Objects.equals(fieldError.getCode(), "Email")) {
                    errorCode = ErrorCode.E_400_INVALID_EMAIL_FORMAT;
                }
            }
            else if ("password".equals(fieldError.getField()) ||
                    "oldPassword".equals(fieldError.getField()) ||
                    "newPassword".equals(fieldError.getField())){
                if (Objects.equals(fieldError.getCode(), "NotBlank")) {
                    errorCode = ErrorCode.E_400_MISSING_PASSWORD;
                } else if (Objects.equals(fieldError.getCode(), "Size")) {
                    errorCode = ErrorCode.E_400_PASSWORD_SIZE;
                }
                else if (Objects.equals(fieldError.getCode(), "Pattern")) {
                    errorCode = ErrorCode.E_400_PASSWORD_COMPLEXITY;
                }
            }
            else if ("nickname".equals(fieldError.getField())){
                if (Objects.equals(fieldError.getCode(), "NotBlank")) {
                    errorCode = ErrorCode.E_400_MISSING_NICKNAME;
                }
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


//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
//
//        return ResponseEntity
//                .status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .body(new ErrorResponse(ErrorCode.E_500));
//    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ErrorResponse> handleExpired(ExpiredJwtException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(ErrorCode.E_401_REFRESH_EXPIRED));
    }
}
