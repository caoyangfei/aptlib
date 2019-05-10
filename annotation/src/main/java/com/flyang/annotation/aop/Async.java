package com.flyang.annotation.aop;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * @author caoyangfei
 * @ClassName Async
 * @date 2019/4/23
 * ------------- Description -------------
 * RxJava,异步地执行app中的方法
 */
@Target({METHOD})
@Retention(CLASS)
public @interface Async {
}
