package com.exchange.core.exception;

import com.exchange.core.domain.enums.CodeEnum;

public class ExchangeException extends RuntimeException {
	private static final long serialVersionUID = 5752254573896810488L;
	
	public final CodeEnum codeEnum;

    public ExchangeException(CodeEnum codeEnum) {
        super(codeEnum.getMessage());
        this.codeEnum = codeEnum;
    }

    public ExchangeException(CodeEnum codeEnum, Throwable cause) {
        super(codeEnum.getMessage(), cause);
        this.codeEnum = codeEnum;
    }

    public ExchangeException(CodeEnum codeEnum, String message) {
        super(message);
        this.codeEnum = codeEnum;
    }

    public ExchangeException(CodeEnum codeEnum, String message, Throwable cause) {
        super(message, cause);
        this.codeEnum = codeEnum;
    }

}
