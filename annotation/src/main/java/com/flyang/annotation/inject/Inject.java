package com.flyang.annotation.inject;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author caoyangfei
 * @ClassName Inject
 * @date 2019/5/6
 * ------------- Description -------------
 * 用于标记需要被注入类，最近都将插入到标记了#IMethod的方法中
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface Inject {
    String value() default "";
}
