package com.idempotent.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName： Idempotent.java
 * @author： Tangqh
 * @version： 1.0.0
 * @createTime： 2021年12月10日
 * @功能描述： 幂等注解类
 */
@Inherited
@Target(ElementType.METHOD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Idempotent {


    /**
     * 是否做幂等处理
     * false：非幂等
     * true：幂等
     * @return
     */
    boolean isIdempotent() default false;

    /**
     * 有效期
     * 默认：1
     * 有效期要大于程序执行时间，否则请求还是可能会进来
     * @return
     */
    int expireTime() default 1;

    /**
     * 时间单位
     * 默认：s
     * @return
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 提示信息，可自定义
     * @return
     */
    String info() default "重复请求，请稍后重试";

    /**
     * 是否在业务完成后删除key
     * true:删除
     * false:不删除
     * @return
     */
    boolean delKey() default false;

    /**
     * 是否针对对同一来源ip进行幂等校验
     * true:校验
     * false:不校验
     * @return
     */
    boolean haveIp() default false;
}
