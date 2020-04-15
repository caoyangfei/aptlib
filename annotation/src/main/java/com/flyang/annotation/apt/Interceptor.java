package com.flyang.annotation.apt;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author caoyangfei
 * @ClassName Interceptor
 * @date 2019/4/24
 * ------------- Description -------------
 * 路由拦截器
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface Interceptor {
    //拦截器名
    String value();
}
