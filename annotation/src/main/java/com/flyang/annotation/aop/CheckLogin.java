package com.flyang.annotation.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author caoyangfei
 * @ClassName CheckLogin
 * @date 2019/5/12
 * ------------- Description -------------
 * 检查用户是否登陆注解，通过aop切片的方式在编译期间织入源代码中
 * 功能：检查用户是否登陆，未登录提示登录，不会执行下面的逻辑
 * <p>
 * example:
 * @Pointcut("execution(@com.flyang.annotation.aop.CheckLogin * *(..))")//方法切入点
 * </p>
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface CheckLogin {
}
