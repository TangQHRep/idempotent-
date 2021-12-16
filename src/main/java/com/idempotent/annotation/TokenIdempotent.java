package com.idempotent.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @ClassName： TokenIdempotent.java
 * @author： Tangqh
 * @version： 1.0.0
 * @createTime： 2021年12月15日
 * @功能描述： 幂等注解类 基于token
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TokenIdempotent {

    //TODO 可以自定义实现个性内容，不断优化补充
}
