package com.flyang.aop.aspect;

import com.flyang.basic.log.LogUtils;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.util.Arrays;


/**
 * @author yangfei.cao
 * @ClassName aptlib_demo
 * @date 2019/3/28
 * ------------- Description -------------
 * <p>
 * 将方法的入参和出参都打印出来,可以用于调试
 */
@Aspect
public class LogMethodAspect {

    @Pointcut("@within(com.flyang.annotation.aop.LogMethod)||@annotation(com.flyang.annotation.aop.LogMethod)")
    public void withinAnnotatedClass() {
    } // @DebugLog 修饰的类、接口的 Join Point

    // synthetic 是内部类编译后添加的修饰语，所以 !synthetic 表示非内部类的

    @Pointcut("execution(!synthetic * *(..)) && withinAnnotatedClass()")
    public void methodInsideAnnotatedType() {
    } // 执行 @DebugLog 修饰的类、接口中的方法，不包括内部类中方法


    @Pointcut("execution(!synthetic *.new(..)) && withinAnnotatedClass()")
    public void constructorInsideAnnotatedType() {
    } // 执行 @DebugLog 修饰的类中的构造函数，不包括内部类的构造函数

    @Pointcut("execution(@com.flyang.annotation.aop.LogMethod * *(..)) || methodInsideAnnotatedType()")
    public void method() {
    } // 执行 @DebugLog 修饰的方法，或者 @DebugLog 修饰的类、接口中的方法

    @Pointcut("execution(@com.flyang.annotation.aop.LogMethod *.new(..)) || constructorInsideAnnotatedType()")
    public void constructor() {
    } // 执行 @DebugLog 修饰的构造函数，或者 @DebugLog 修饰的类中的构造函数


    @Around("withinAnnotatedClass()")
    public Object doLogMethod(final ProceedingJoinPoint joinPoint) throws Throwable {
        return logMethod(joinPoint);
    }

    private Object logMethod(final ProceedingJoinPoint joinPoint) throws Throwable {
        LogUtils.w(joinPoint.getSignature().toShortString() + " Args : " + (joinPoint.getArgs() != null ? Arrays.deepToString(joinPoint.getArgs()) : ""));
        Object result = joinPoint.proceed();
        String type = ((MethodSignature) joinPoint.getSignature()).getReturnType().toString();
        LogUtils.w(joinPoint.getSignature().toShortString() + " Result : " + ("void".equalsIgnoreCase(type) ? "void" : result));
        return result;
    }
}
