package com.flyang.aop.aspect;


import com.flyang.annotation.aop.Safe;
import com.flyang.basic.data.ObjectUtils;
import com.flyang.basic.data.ReflectUtils;
import com.flyang.basic.log.LogUtils;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;


/**
 * @author yangfei.cao
 * @ClassName aptlib_demo
 * @date 2019/3/28
 * ------------- Description -------------
 * <p>
 * 可以安全地执行方法,而无需考虑是否会抛出运行时异常
 * 支持在捕获异常的时候进行监听
 */
@Aspect
public class SafeAspect {

    private static final String POINTCUT_METHOD = "execution(@com.flyang.annotation.aop.Safe * *(..))";

    @Pointcut(POINTCUT_METHOD)
    public void methodAnnotatedWithSafe() {
    }

    @Around("methodAnnotatedWithSafe()")
    public Object safeMethod(final ProceedingJoinPoint joinPoint) throws Throwable {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        Safe safe = method.getAnnotation(Safe.class);

        Object result = null;
        try {
            result = joinPoint.proceed();
        } catch (Throwable e) {
            LogUtils.e(getStringFromException(e));
            String callBack = safe.callBack();
            if (ObjectUtils.isNotBlank(callBack)) {
                try {
                    ReflectUtils.on(joinPoint.getTarget()).call(callBack);
                } catch (RuntimeException exception) {
                    exception.printStackTrace();
                }
            }
        }
        return result;
    }

    private static String getStringFromException(Throwable ex) {
        StringWriter errors = new StringWriter();
        ex.printStackTrace(new PrintWriter(errors));
        return errors.toString();
    }
}
