package com.flyang.annotation.inject;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author caoyangfei
 * @ClassName IMethod
 * @date 2019/5/6
 * ------------- Description -------------
 * 用于标记注册代码将插入到此方法中
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface IMethod {
    String value() default "";
}
