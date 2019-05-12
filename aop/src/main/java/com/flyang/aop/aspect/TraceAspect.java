package com.flyang.aop.aspect;


import android.annotation.SuppressLint;

import com.flyang.annotation.aop.Trace;
import com.flyang.aop.modle.StopWatch;
import com.flyang.util.data.ObjectUtils;
import com.flyang.util.log.LogUtils;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;


/**
 * @author yangfei.cao
 * @ClassName aptlib_demo
 * @date 2019/3/28
 * ------------- Description -------------
 * <p>
 * 用于追踪某个方法花费的时间,可以用于性能调优的评判
 * 支持追踪匿名内部类中的方法
 */
@Aspect
public class TraceAspect {

    private static final String POINTCUT_METHOD = "execution(@com.flyang.annotation.aop.Trace * *(..))";

    private static final String POINTCUT_CONSTRUCTOR = "execution(@com.flyang.annotation.aop.Trace *.new(..))";

    private static final int ns = 1000 * 1000;

    @Pointcut(POINTCUT_METHOD)
    public void methodAnnotatedWithTrace() {
    }

    @Pointcut(POINTCUT_CONSTRUCTOR)
    public void constructorAnnotatedTrace() {
    }

    @Around("methodAnnotatedWithTrace() || constructorAnnotatedTrace()")
    public Object traceMethod(final ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();

        Trace trace = methodSignature.getMethod().getAnnotation(Trace.class);
        if (trace != null && !trace.enable()) {
            return joinPoint.proceed();
        }

        String className = methodSignature.getDeclaringType().getSimpleName();
        String methodName = methodSignature.getName();
        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Object result = joinPoint.proceed();
        stopWatch.stop();

        if (ObjectUtils.isBlank(className)) {
            className = "Anonymous class";
        }
        LogUtils.i(className, buildLogMessage(methodName, stopWatch.getElapsedTime()));
        return result;
    }

    /**
     * @param methodName     方法名
     * @param methodDuration 方法的持续时间（以毫秒为单位）
     * @return
     */
    @SuppressLint("DefaultLocale")
    private static String buildLogMessage(String methodName, long methodDuration) {

        if (methodDuration > 10 * ns) {
            return String.format("%s() take %d ms", methodName, methodDuration / ns);
        } else if (methodDuration > ns) {
            return String.format("%s() take %dms %dns", methodName, methodDuration / ns,
                    methodDuration % ns);
        } else {
            return String.format("%s() take %dns", methodName, methodDuration % ns);
        }
    }
}
