package com.flyang.demo.aop;

import com.flyang.basic.log.LogUtils;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;


/**
 * @author caoyangfei
 * @ClassName CheckLoginAspect
 * @date 2019/5/12
 * ------------- Description -------------
 * 通过CheckLogin注解检查用户是否登陆注解，通过aop切片的方式在编译期间织入源代码中
 * 功能：检查用户是否登陆，未登录则提示登录，不会执行下面的逻辑
 */
@Aspect
public class CheckLoginAspect {

    @Pointcut("execution(@com.flyang.annotation.aop.CheckLogin * *(..))")//方法切入点
    public void methodAnnotated() {
    }

    @Around("methodAnnotated()")//在连接点进行方法替换
    public void aroundJoinPoint(ProceedingJoinPoint joinPoint) throws Throwable {
        if (true) {
            LogUtils.tag("CheckLoginAspect:").d("aroundJoinPoint: 未登录");
            //未登录
            return;
        }
        joinPoint.proceed();//执行原方法
    }
}

