package com.flyang.annotation.aop;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;

/**
 * @author caoyangfei
 * @ClassName Safe
 * @date 2019/4/23
 * ------------- Description -------------
 * 可以安全地执行方法,而无需考虑是否会抛出运行时异常
 * 支持在捕获异常的时候进行监听
 */
@Target({METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Safe {

    String callBack() default "";
}
