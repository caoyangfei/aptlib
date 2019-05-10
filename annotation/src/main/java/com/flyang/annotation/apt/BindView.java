package com.flyang.annotation.apt;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author yangfei.cao
 * @ClassName aptlib_demo
 * @date 2019/3/30
 * ------------- Description -------------
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.FIELD)
public @interface BindView {
    String value();
}
