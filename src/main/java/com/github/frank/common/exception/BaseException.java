package com.github.frank.common.exception;

import lombok.Getter;

/**
 * @author Frank An
 */
@Getter
sealed public class BaseException extends RuntimeException
        permits BusinessException, TechnicalException {

    private final String code;

    public BaseException(String message, String code) {
        super(message);
        this.code = code;
    }

}
