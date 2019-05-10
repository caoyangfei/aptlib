package com.flyang.aop.aspect;

import com.flyang.annotation.aop.Cacheable;
import com.flyang.basic.cache.CacheMemoryUtils;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.io.Serializable;
import java.lang.reflect.Method;


/**
 * @author yangfei.cao
 * @ClassName aptlib_demo
 * @date 2019/3/28
 * ------------- Description -------------
 * 缓存
 */
@Aspect
public class CacheAspect {

    @Around("execution(!synthetic * *(..)) && onCacheMethod()")
    public Object doCacheMethod(final ProceedingJoinPoint joinPoint) throws Throwable {
        return cacheMethod(joinPoint);
    }

    //    execution(@com.example.annotation.Cacheable * *(..))
//    @Pointcut("@within(com.example.annotation.Cacheable)||@annotation(com.example.annotation.Cacheable)")
    @Pointcut("execution(@com.flyang.annotation.aop.Cacheable * *(..))||@annotation(com.flyang.annotation.aop.Cacheable)")
    public void onCacheMethod() {
    }

    private Object cacheMethod(final ProceedingJoinPoint joinPoint) throws Throwable {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        Cacheable cacheable = method.getAnnotation(Cacheable.class);
        Object result = null;
        if (cacheable != null) {
            String key = cacheable.key();
            int expiry = cacheable.expiry();

            result = joinPoint.proceed();
            if (expiry > 0) {
                CacheMemoryUtils.getInstance().put(key, (Serializable) result, expiry);
            } else {
                CacheMemoryUtils.getInstance().put(key, (Serializable) result);
            }
        } else {
            // 不影响原来的流程
            result = joinPoint.proceed();
        }

        return result;
    }
}
