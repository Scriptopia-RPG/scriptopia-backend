package com.scriptopia.demo.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    //Auth
    A_401001("AUTH_401001", "로그인이 필요합니다.", HttpStatus.UNAUTHORIZED),
    A_403001("AUTH_403001", "접근 권한이 없습니다.", HttpStatus.FORBIDDEN),

    //User
    U_400001("USER_400001","이메일 또는 비밀번호가 올바르지 않습니다.", HttpStatus.UNAUTHORIZED);
    private final String code;
    private final String message;
    private final HttpStatus status;

}
