package com.flyang.complier.model;

import com.flyang.annotation.apt.inter.ListenerClass;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.VariableElement;

import static javax.lang.model.element.ElementKind.METHOD;

/**
 * @author yangfei.cao
 * @ClassName aptlib_demo-master
 * @date 2019/4/29
 * ------------- Description -------------
 * 注解方法
 */
public class MethodViewModel implements ViewModel {

    //注解传入的ids
    private Set<String> mResIds;
    //param
    private ExecutableElement methodElement;
    private final ListenerClass listenerClass;

    public MethodViewModel(Element element, Class<? extends Annotation> annotationClass) throws IllegalArgumentException {
        // 判断是否是方法
        if (!(element instanceof ExecutableElement) || element.getKind() != METHOD) {
            throw new IllegalStateException(
                    String.format("@%s annotation must be on a method.", annotationClass.getSimpleName()));
        }
        methodElement = (ExecutableElement) element;

        // 获取注解的值
        mResIds = new HashSet();
        Annotation annotation = element.getAnnotation(annotationClass);
        listenerClass = annotationClass.getAnnotation(ListenerClass.class);
        Method annotationValue = null;
        try {
            annotationValue = annotationClass.getDeclaredMethod("value");
            if (annotationValue.getReturnType() != String[].class) {
                throw new IllegalStateException(
                        String.format("@%s annotation value() type not String[].", annotationClass));
            }
            mResIds.addAll(Arrays.asList((String[]) annotationValue.invoke(annotation)));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    /**
     * 获取方法名
     *
     * @return
     */
    public Name getSimpleName() {
        return methodElement.getSimpleName();
    }

    /**
     * 获取注解的值
     *
     * @return
     */
    public Set<String> getResIds() {
        return mResIds;
    }

    /**
     * 获取params
     *
     * @return
     */
    public List<? extends VariableElement> getParamsList() {
        return methodElement.getParameters();
    }

    /**
     * 获取注解监听事件
     *
     * @return
     */
    public ListenerClass getListenerClass() {
        return listenerClass;
    }
}
