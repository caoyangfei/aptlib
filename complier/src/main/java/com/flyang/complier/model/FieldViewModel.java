package com.flyang.complier.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Name;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

/**
 * @author yangfei.cao
 * @ClassName aptlib_demo-master
 * @date 2019/4/29
 * ------------- Description -------------
 */
public class FieldViewModel implements ViewModel {

    private VariableElement mVariableElement;
    //id
    private String mResId;

    public FieldViewModel(Element element, Class<? extends Annotation> annotationClass) throws IllegalArgumentException {
        // 判断是否是成员变量
        if (element.getKind() != ElementKind.FIELD) {
            throw new IllegalArgumentException(String.format("Only field can be annotated with @%s", annotationClass.getSimpleName()));
        }
        mVariableElement = (VariableElement) element;
        // 获取注解和id值
        Annotation annotation = element.getAnnotation(annotationClass);
        Method annotationValue = null;
        try {
            annotationValue = annotationClass.getDeclaredMethod("value");
            if (annotationValue.getReturnType() != String.class) {
                throw new IllegalStateException(
                        String.format("@%s annotation value() type not String.", annotationClass));
            }
            mResId = (String) annotationValue.invoke(annotation);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取变量名
     *
     * @return Name
     */
    public Name getSimpleName() {
        return mVariableElement.getSimpleName();
    }

    /**
     * 获取id
     *
     * @return int
     */
    public String getFieldResId() {
        return mResId;
    }

    /**
     * 获取变量类型
     *
     * @return TypeMirror
     */
    public TypeMirror getFieldType() {
        return mVariableElement.asType();
    }

    /**
     * 获取变量类型，如TextView
     *
     * @return
     */
    public String getFieldClass() {
        String className = getFieldType().toString();
        return className.substring(className.lastIndexOf(".") + 1);
    }
}
