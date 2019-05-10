package com.flyang.annotation.aop;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.METHOD;

/**
 * @author caoyangfei
 * @ClassName HookMethod
 * @date 2019/4/23
 * ------------- Description -------------
 * 可以在调用某个方法之前、以及之后进行hook，比较适合埋点的场景，
 * 可以单独使用也可以跟任何自定义注解配合使用。也支持在匿名内部类中使用
 */
@Target({METHOD, CONSTRUCTOR})
@Retention(RetentionPolicy.RUNTIME)
public @interface HookMethod {

    String beforeMethod() default "";

    String afterMethod() default "";
}
