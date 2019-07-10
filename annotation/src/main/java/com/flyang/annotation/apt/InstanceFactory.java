package com.flyang.annotation.apt;


/**
 * Created by baixiaokang on 16/10/8.
 */

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author caoyangfei
 * @ClassName InstanceFactory
 * @date 2019/7/9
 * ------------- Description -------------
 * 实例化注解,会被主动添加到实例化工厂,自动生成new来替换掉反射的newInstance代码
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface InstanceFactory {

}


