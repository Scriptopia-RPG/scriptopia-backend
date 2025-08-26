package com.scriptopia.demo.exception;

import com.scriptopia.demo.domain.ErrorCode;
import lombok.Getter;


@Getter
public class CustomException extends RuntimeException {
    private final ErrorCode errorCode;

    public CustomException(final ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

}
