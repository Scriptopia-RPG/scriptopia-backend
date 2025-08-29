package com.scriptopia.demo.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {


    //400 Bad Request
    E_400("E400000", "잘못된 요청 형식입니다.", HttpStatus.BAD_REQUEST),

    E_400_MISSING_EMAIL("E400001", "이메일은 필수 입력 값입니다.", HttpStatus.BAD_REQUEST),
    E_400_INVALID_EMAIL_FORMAT("E400002","이메일 형식이 올바르지 않습니다.",HttpStatus.BAD_REQUEST),
    E_400_INVALID_CODE("E400003", "인증 코드는 6자리 숫자여야 합니다.", HttpStatus.BAD_REQUEST),
    E_400_MISSING_PASSWORD("E400004", "비밀번호는 필수 입력 값입니다.", HttpStatus.BAD_REQUEST),
    E_400_PASSWORD_SIZE("E400005", "비밀번호는 8~20자리여야 합니다.", HttpStatus.BAD_REQUEST),
    E_400_PASSWORD_COMPLEXITY("E400006", "비밀번호는 소문자, 숫자, 특수문자를 포함해야 합니다.", HttpStatus.BAD_REQUEST),
    E_400_MISSING_NICKNAME("E400007", "닉네임은 필수 입력 값입니다.", HttpStatus.BAD_REQUEST),


    //401 Unauthorized
    E_401_INVALID_CREDENTIALS("E401001","이메일 또는 비밀번호가 올바르지 않습니다.", HttpStatus.UNAUTHORIZED),

    E_401_CODE_MISMATCH("E401002","인증 코드가 올바르지 않습니다.", HttpStatus.UNAUTHORIZED),

    //403 Forbidden
    E_403_ROLE_FORBIDDEN("E403001", "접근 권한이 없습니다.", HttpStatus.FORBIDDEN),

    //409 Conflict
    E_409_EMAIL_TAKEN("E409001", "이미 사용 중인 이메일입니다.", HttpStatus.CONFLICT),
    E_409_NICKNAME_TAKEN("E409002", "이미 사용 중인 닉네임입니다.", HttpStatus.CONFLICT),

    //412 Precondition Failed
    E_412_EMAIL_NOT_VERIFIED("E412001", "이메일 인증이 필요합니다.",HttpStatus.PRECONDITION_FAILED),

    //500 Internal Server Error
    E_500("E_500000", "예상치 못한 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String message;
    private final HttpStatus status;

}
