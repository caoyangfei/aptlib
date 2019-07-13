package com.flyang.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author caoyangfei
 * @ClassName Controller
 * @date 2019/6/29
 * ------------- Description -------------
 * 注解管理器持有activity或fragment生命周期
 * <p>
 * 注解在变量上
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Controller {

}
