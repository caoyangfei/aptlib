package com.flyang.annotation.aop;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;

/**
 * @author caoyangfei
 * @ClassName Prefs
 * @date 2019/4/23
 * ------------- Description -------------
 * 将方法返回的结果放入SharedPreferences中
 */
@Target({METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Prefs {

    String key();
}
