package com.flyang.annotation.apt;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author caoyangfei
 * @ClassName ContractFactory
 * @date 2019/7/14
 * ------------- Description -------------
 * 注解生成Contract，关联V层和P层
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface ContractFactory {
    Class<?>[] entites();
}
