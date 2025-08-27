package com.scriptopia.demo.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {


    //400 Bad Request
    E_400("E_400", "잘못된 요청 형식입니다.", HttpStatus.BAD_REQUEST),
    REQ_400_MISSING_EMAIL("REQ_400_EMAIL_REQUIRED", "이메일은 필수 입력 값입니다.", HttpStatus.BAD_REQUEST),
    REQ_400_INVALID_EMAIL_FORMAT("REQ_400_EMAIL","이메일 형식이 올바르지 않습니다.",HttpStatus.BAD_REQUEST),

    //401 Unauthorized
    AUTH_401_INVALID_CREDENTIALS("AUTH_401_CRED","이메일 또는 비밀번호가 올바르지 않습니다.", HttpStatus.UNAUTHORIZED),

    //403 Forbidden
    AUTH_403_ROLE_FORBIDDEN("AUTH_403_ROLE", "접근 권한이 없습니다.", HttpStatus.FORBIDDEN),

    //500 INTERNAL_SERVER_ERROR
    E_500("E_500", "예상치 못한 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
    private final String code;
    private final String message;
    private final HttpStatus status;

}
