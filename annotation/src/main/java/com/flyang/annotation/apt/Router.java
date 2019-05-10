package com.flyang.annotation.apt;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author caoyangfei
 * @ClassName Router
 * @date 2019/4/24
 * ------------- Description -------------
 * 路由
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Router {

    //跳转path
    String[] value();

    //拦截器
    String[] interceptors() default {};
}
