package com.flyang.aop.aspect;

import com.flyang.annotation.aop.Prefs;
import com.flyang.util.data.SPStaticUtils;

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
 * 将方法返回的结果放入SharedPreferences中
 */
@Aspect
public class PrefsAspect {

    @Around("execution(!synthetic * *(..)) && onPrefsMethod()")
    public Object doPrefsMethod(final ProceedingJoinPoint joinPoint) throws Throwable {
        return prefsMethod(joinPoint);
    }

    @Pointcut("@within(com.flyang.annotation.aop.Prefs)||@annotation(com.flyang.annotation.aop.Prefs)")
    public void onPrefsMethod() {
    }

    private Object prefsMethod(final ProceedingJoinPoint joinPoint) throws Throwable {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        Prefs prefs = method.getAnnotation(Prefs.class);
        Object result = null;
        if (prefs!=null) {
            String key = prefs.key();

            result = joinPoint.proceed();
            String type = ((MethodSignature) joinPoint.getSignature()).getReturnType().toString();

            if (!"void".equalsIgnoreCase(type)) {
                String className = ((MethodSignature) joinPoint.getSignature()).getReturnType().getCanonicalName();
                if ("int".equals(className) || "java.lang.Integer".equals(className)) {
                    SPStaticUtils.putInt(key, (Integer) result);
                } else if ("boolean".equals(className) || "java.lang.Boolean".equals(className)) {
                    SPStaticUtils.putBoolean(key,(Boolean) result);
                } else if ("float".equals(className) || "java.lang.Float".equals(className)) {
                    SPStaticUtils.putFloat(key,(Float) result);
                } else if ("long".equals(className) || "java.lang.Long".equals(className)) {
                    SPStaticUtils.putLong(key,(Long) result);
                } else if ("java.lang.String".equals(className)) {
                    SPStaticUtils.putString(key,(String) result);
                } else {
                    SPStaticUtils.putObject(key,result);
                }
            }
        } else {
            // 不影响原来的流程
            result = joinPoint.proceed();
        }

        return result;
    }
}
