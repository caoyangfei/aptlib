package com.flyang.annotation.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * @author caoyangfei
 * @ClassName SingleClick
 * @date 2019/7/9
 * ------------- Description -------------
 * 防止View被连续点击
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface SingleClick {
}
