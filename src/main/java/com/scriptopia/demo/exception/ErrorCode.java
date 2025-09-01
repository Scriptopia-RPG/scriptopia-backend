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
    E_400_REFRESH_REQUIRED("E400008", "리프레쉬 토큰이 필요합니다.", HttpStatus.BAD_REQUEST),
    E_400_PASSWORD_CONFIRM_MISMATCH("E400009", "새 비밀번호와 비밀번호 확인이 일치하지 않습니다.",HttpStatus.BAD_REQUEST),
    E_400_PASSWORD_WHITESPACE("E400010","비밀번호에 공백을 포함할 수 없습니다.",HttpStatus.BAD_REQUEST),
    E_400_INSUFFICIENT_PIA("E400011", "금액이 부족합니다.", HttpStatus.BAD_REQUEST),
    E_400_SELF_PURCHASE("E400012", "자기 물건은 구매할 수 없습니다.", HttpStatus.BAD_REQUEST),
    E_400_INVALID_USER_ITEM_ID("E400013", "잘못된 아이템 ID 형식입니다.", HttpStatus.BAD_REQUEST),
    E_400_ITEM_NOT_OWNED("E400014", "해당 아이템은 사용자가 소유하지 않았습니다.", HttpStatus.BAD_REQUEST),
    E_400_ITEM_NOT_TRADE_ABLE("E400015", "해당 아이템은 현재 경매장에 올릴 수 없습니다.", HttpStatus.BAD_REQUEST),
    E_400_ITEM_ALREADY_REGISTERED("E400016", "이미 경매장에 등록된 아이템입니다.", HttpStatus.BAD_REQUEST),
    E_400_INVALID_AMOUNT("E400017", "금액은 0보다 커야 합니다.", HttpStatus.BAD_REQUEST),
    E_400_MISSING_JWT("E400018", "토큰 값이 비어있습니다.", HttpStatus.BAD_REQUEST),
    E_400_PIA_ITEM_DUPLICATE("E400019", "이미 존재하는 PIA 아이템 이름입니다.", HttpStatus.BAD_REQUEST),
    E_400_INVALID_REQUEST("E400020", "이름이나, 금액이 비어있습니다.", HttpStatus.BAD_REQUEST),
    E_400_GAME_ALREADY_IN_PROGRESS("E400021", "진행 중인 게임이 이미 존재합니다.", HttpStatus.BAD_REQUEST),
    E_400_ITEM_NO_USES_LEFT("E400025", "아이템 사용 가능 횟수가 남아있지 않습니다.", HttpStatus.BAD_REQUEST),



    //401 Unauthorized
    E_401("401000", "인증되지 않은 요청입니다. (토큰 없음, 만료, 잘못됨)",HttpStatus.UNAUTHORIZED),
    E_401_INVALID_CREDENTIALS("E401001","이메일 또는 비밀번호가 올바르지 않습니다.", HttpStatus.UNAUTHORIZED),
    E_401_CODE_MISMATCH("E401002","인증 코드가 올바르지 않습니다.", HttpStatus.UNAUTHORIZED),
    E_401_REFRESH_EXPIRED("E401003","리프레쉬 토큰이 만료되었습니다.", HttpStatus.UNAUTHORIZED),
    E_401_CURRENT_PASSWORD_MISMATCH("E401004","현재 비밀번호가 올바르지 않습니다.",HttpStatus.UNAUTHORIZED),
    E_401_INVALID_SIGNATURE("E401005", "JWT 서명이 유효하지 않습니다.", HttpStatus.UNAUTHORIZED),
    E_401_MALFORMED("E401006", "JWT 형식이 올바르지 않습니다.", HttpStatus.UNAUTHORIZED),
    E_401_EXPIRED_JWT("E401007", "JWT 토큰이 만료되었습니다.", HttpStatus.UNAUTHORIZED),
    E_401_UNSUPPORTED_JWT("E401008", "지원하지 않는 JWT 형식입니다.", HttpStatus.UNAUTHORIZED),
    E_401_NOT_EQUAL_SHARED_GAME("E401009", "사용자가 공유한 게임이 아닙니다.", HttpStatus.UNAUTHORIZED),

    //403 Forbidden
    E_403("E403000", "접근 권한이 없습니다.", HttpStatus.FORBIDDEN),
    E_403_DEVICE_MISMATCH("E403001", "요청 디바이스와 토큰의 디바이스가 일치하지 않습니다.", HttpStatus.FORBIDDEN),
    E_403_SETTLEMENT_FORBIDDEN("E403002", "해당 정산 내역에 접근할 권한이 없습니다.", HttpStatus.FORBIDDEN),


    //404 Not Found
    E_404_REFRESH_NOT_FOUND("E404001", "유효한 리프레시 세션을 찾을 수 없습니다.",HttpStatus.NOT_FOUND),
    E_404_USER_NOT_FOUND("E404002","사용자를 찾을 수 없습니다.",HttpStatus.NOT_FOUND),
    E_404_AUCTION_NOT_FOUND("E404003", "해당 아이템이 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    E_404_SETTLEMENT_NOT_FOUND("E404004","정산 내역을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    E_404_SHARED_GAME_NOT_FOUND("E404005", "공유된 게임을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    E_404_GAME_SESSION_NOT_FOUND("E404006", "게임을 불러올 수 없습니다.", HttpStatus.NOT_FOUND),


    //409 Conflict
    E_409_EMAIL_TAKEN("E409001", "이미 사용 중인 이메일입니다.", HttpStatus.CONFLICT),
    E_409_NICKNAME_TAKEN("E409002", "이미 사용 중인 닉네임입니다.", HttpStatus.CONFLICT),
    E_409_REFRESH_REUSE_DETECTED("E409003", "리프레시 토큰 재사용이 감지되었습니다.", HttpStatus.CONFLICT),
    E_409_PASSWORD_SAME_AS_OLD("E409004","기존 비밀번호와 동일한 비밀번호는 사용할 수 없습니다.",HttpStatus.CONFLICT),
    E_409_ALREADY_CONFIRMED("E409005","이미 정산이 완료된 항목입니다.", HttpStatus.CONFLICT),


    //412 Precondition Failed
    E_412_EMAIL_NOT_VERIFIED("E412001", "이메일 인증이 필요합니다.",HttpStatus.PRECONDITION_FAILED),

    //500 Internal Server Error
    E_500("E_500000", "예상치 못한 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    E_500_TOKEN_HASHING_FAILED("E_500001","리프레쉬 토큰 해싱에 실패했습니다.",HttpStatus.INTERNAL_SERVER_ERROR),
    E_500_EXTERNAL_API_ERROR("E500001", "외부 게임 API 호출에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);


    private final String code;
    private final String message;
    private final HttpStatus status;

}
