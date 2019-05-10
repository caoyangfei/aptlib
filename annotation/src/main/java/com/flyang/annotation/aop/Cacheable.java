package com.flyang.annotation.aop;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;

/**
 * @author caoyangfei
 * @ClassName Cacheable
 * @date 2019/4/23
 * ------------- Description -------------
 * 缓存
 */

@Target({METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Cacheable {

    String key();

    int expiry() default -1; // 过期时间,单位是秒
}
