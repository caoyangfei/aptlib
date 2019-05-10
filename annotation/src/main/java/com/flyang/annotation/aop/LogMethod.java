package com.flyang.annotation.aop;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * @author caoyangfei
 * @ClassName LogMethod
 * @date 2019/4/23
 * ------------- Description -------------
 * 将方法的入参和出参都打印出来,可以用于调试
 */
@Target({METHOD})
@Retention(CLASS)
public @interface LogMethod {
}
