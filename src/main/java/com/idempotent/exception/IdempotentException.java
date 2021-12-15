package com.idempotent.exception;


/**
 * @ClassName： IdempotentException.java
 * @author： Tangqh
 * @version： 1.0.0
 * @createTime： 2021年12月10日
 * @功能描述：自定义幂等错误
 */
public class IdempotentException extends RuntimeException{

    public IdempotentException() {
        super();
    }



    public IdempotentException(String message) {
        super(message);
    }

    public IdempotentException(String message, Throwable cause) {
        super(message, cause);
    }

    public IdempotentException(Throwable cause) {
        super(cause);
    }

    protected IdempotentException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
