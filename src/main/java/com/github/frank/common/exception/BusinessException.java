package com.github.frank.common.exception;

/**
 * @author Frank An
 */
public final class BusinessException extends BaseException {
    public BusinessException(String message, String code) {
        super(message, code);
    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage(), errorCode.getCode());
    }
}
