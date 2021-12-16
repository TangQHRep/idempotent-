package com.idempotent.common;

import com.idempotent.exception.IdempotentException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @ClassName： TokenIdempotent.java
 * @author： Tangqh
 * @version： 1.0.0
 * @createTime： 2021年12月15日
 * @功能描述： 全局异常处理
 */
@Slf4j
@ControllerAdvice
public class WebControllerAdvice {

    @ResponseBody
    @ExceptionHandler(IdempotentException.class)
    public ServerResponse serviceExceptionHandler(IdempotentException se) {
        return ServerResponse.error(se.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public ServerResponse exceptionHandler(Exception e) {
        log.error("Exception: ", e);
        return ServerResponse.error(ResponseCode.SERVER_ERROR);
    }

}
