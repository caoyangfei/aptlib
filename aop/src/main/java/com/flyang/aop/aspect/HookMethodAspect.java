package com.flyang.aop.aspect;

import com.flyang.annotation.aop.HookMethod;
import com.flyang.basic.data.ObjectUtils;
import com.flyang.basic.data.ReflectUtils;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;


/**
 * @author yangfei.cao
 * @ClassName aptlib_demo
 * @date 2019/3/28
 * ------------- Description -------------
 *
 * 可以在调用某个方法之前、以及之后进行hook，比较适合埋点的场景，
 * 可以单独使用也可以跟任何自定义注解配合使用。也支持在匿名内部类中使用
 */
@Aspect
public class HookMethodAspect {

    private static final String POINTCUT_METHOD = "execution(@com.flyang.annotation.aop.HookMethod * *(..))";

    private static final String POINTCUT_CONSTRUCTOR = "execution(@com.flyang.annotation.aop.HookMethod *.new(..))";

    @Pointcut(POINTCUT_METHOD)
    public void methodAnnotatedWithHookMethod() {
    }

    @Pointcut(POINTCUT_CONSTRUCTOR)
    public void constructorAnnotatedHookMethod() {
    }

    @Around("methodAnnotatedWithHookMethod() || constructorAnnotatedHookMethod()")
    public void hookMethod(final ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        HookMethod hookMethod = method.getAnnotation(HookMethod.class);

        if (hookMethod==null) return;

        String beforeMethod = hookMethod.beforeMethod();
        String afterMethod = hookMethod.afterMethod();

        if (ObjectUtils.isNotBlank(beforeMethod)) {
            try {
                ReflectUtils.on(joinPoint.getTarget()).call(beforeMethod);
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }

        joinPoint.proceed();

        if (ObjectUtils.isNotBlank(beforeMethod)) {
            try {
                ReflectUtils.on(joinPoint.getTarget()).call(afterMethod);
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
    }
}
